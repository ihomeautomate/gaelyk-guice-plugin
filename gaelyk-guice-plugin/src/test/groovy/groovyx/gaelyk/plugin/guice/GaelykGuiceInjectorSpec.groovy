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

import com.google.inject.BindingAnnotation
import com.google.inject.Injector
import com.google.inject.Key
import com.google.inject.name.Names
import groovyx.gaelyk.plugin.guice.exception.InvalidInjectionException
import java.lang.annotation.Retention
import java.lang.annotation.Target
import spock.lang.Specification
import spock.lang.Unroll
import static com.google.inject.Key.get
import static java.lang.annotation.ElementType.*
import static java.lang.annotation.RetentionPolicy.RUNTIME

class GaelykGuiceInjectorSpec extends Specification {
	Injector injector = Mock(Injector)
	Binding binding = new Binding()
	GaelykGuiceInjector gaelykGuiceInjector = new GaelykGuiceInjector(injector, binding)

	void "throws an exception if key is not @Named based"() {
		when:
		gaelykGuiceInjector.inject get(String, UnsupportedKeyAnnotation)

		then:
		InvalidInjectionException e = thrown()
		e.message == "Unsupported injection key annotation: 'groovyx.gaelyk.plugin.guice.UnsupportedKeyAnnotation'" +
			", only 'com.google.inject.name.Named' is supported as key annotation"
	}

	void "injection works as expected"() {
		given:
		def namedKey = Key.get(String, Names.named('namedInjectedInstance'))
		when:
		gaelykGuiceInjector.inject String, namedKey

		then:
		1 * injector.getInstance(String) >> 'injectedByType'
		1 * injector.getInstance(namedKey) >> 'injectedByName'
		binding.string == 'injectedByType'
		binding.namedInjectedInstance == 'injectedByName'
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
		scenatrio           | bindingName             | injectionSpecification
		'injection by type' | 'string'                | String
		'injection by name' | 'namedInjectedInstance' | Key.get(String, Names.named('namedInjectedInstance'))
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


@BindingAnnotation @Target([FIELD, PARAMETER, METHOD]) @Retention(RUNTIME)
@interface UnsupportedKeyAnnotation {}
