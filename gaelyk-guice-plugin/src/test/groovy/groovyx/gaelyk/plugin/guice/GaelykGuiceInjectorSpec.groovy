/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package groovyx.gaelyk.plugin.guice

import com.google.inject.Injector
import com.google.inject.name.Names
import com.google.inject.servlet.RequestParameters
import groovyx.gaelyk.plugin.guice.exception.InvalidInjectionException
import spock.lang.Specification
import spock.lang.Unroll
import static com.google.inject.Key.get

class GaelykGuiceInjectorSpec extends Specification {
	Injector injector = Mock(Injector)
	Binding binding = new Binding()
	GaelykGuiceInjector gaelykGuiceInjector = new GaelykGuiceInjector(injector, binding)

	void "injection works as expected"() {
		given:
		def namedKey = get(String, Names.named('namedInjectedInstance'))
		def qualifierKey = get(String, RequestParameters)

		when:
		gaelykGuiceInjector.inject String, namedKey, qualifierKey

		then:
		1 * injector.getInstance(String) >> 'injectedByType'
		1 * injector.getInstance(namedKey) >> 'injectedByName'
		1 * injector.getInstance(qualifierKey) >> 'injectedByQualifier'
		binding.string == 'injectedByType'
		binding.namedInjectedInstance == 'injectedByName'
		binding.requestParameters == 'injectedByQualifier'
	}

	@Unroll
	void "throws an exception if binding has already a value for a name that is about to be bound - #scenario"() {
		given:
		binding[bindingName] = 'existing value'

		when:
		gaelykGuiceInjector.inject injectionSpecification

		then:
		InvalidInjectionException e = thrown()
		e.message == "Script binding already contains a value for '$bindingName' with class: " +
			"'java.lang.String' and value: 'existing value'"

		where:
		scenario                 | bindingName             | injectionSpecification
		'injection by type'      | 'string'                | String
		'injection by name'      | 'namedInjectedInstance' | get(String, Names.named('namedInjectedInstance'))
		'injection by qualifier' | 'requestParameters'     | get(String, RequestParameters)
	}

	void "throws an exception if an invalid injection specification is passed"() {
		when:
		gaelykGuiceInjector.inject 1

		then:
		InvalidInjectionException e = thrown()
		e.message == 'Only classes and com.google.inject.Key instances are allowed as injection specifiactions ' +
			"but you passed an instance of: 'java.lang.Integer' with value: '1'"
	}
}