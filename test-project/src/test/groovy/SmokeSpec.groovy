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

import com.google.inject.Injector
import com.google.inject.name.Names
import groovyx.gaelyk.plugin.guice.CustomQualifier
import groovyx.gaelyk.plugin.guice.InjectDependenciesCategory
import groovyx.gaelyk.spock.ConventionalGaelykUnitSpec
import spock.util.mop.Use
import static com.google.inject.Key.get

@Use(InjectDependenciesCategory)
class SmokeSpec extends ConventionalGaelykUnitSpec {
	Injector injector = Mock(Injector)

	void setup() {
		smoke.injector = injector
	}

	void "groovlets that use injection can be unit tested"() {
		given:
		injector.getInstance(String) >> 'Hello world!'
		injector.getInstance(get(Integer, Names.named('leet'))) >> 1337
		injector.getInstance(get(String, CustomQualifier)) >> 'Hello Gaelyk!'

		when:
		smoke.get()

		then:
		smoke.request.string == 'Hello world!'
		smoke.request.manuallyInjected == 'Hello world!'
		smoke.request.leet == 1337
		smoke.request.customQualifier == 'Hello Gaelyk!'
	}
}
