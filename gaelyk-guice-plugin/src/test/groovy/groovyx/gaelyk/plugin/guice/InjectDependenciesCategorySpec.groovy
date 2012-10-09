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
import com.google.inject.Key
import com.google.inject.name.Names
import groovyx.gaelyk.plugins.PluginBaseScript
import spock.lang.Specification
import spock.util.mop.Use
import static com.google.inject.Key.get

@Use(InjectDependenciesCategory)
class InjectCategorySpec extends Specification {
	def script = new InjectCategorySpecScript()
	Injector injector = Mock(Injector)
	Binding binding = new Binding(injector: injector)

	void setup() {
		script.binding = binding
	}

	void "inject calls are delegated to GaelykGuiceInjector"() {
		given:
		GaelykGuiceInjector gaelykGuiceInjector = GroovyMock(GaelykGuiceInjector, global: true)
		Key key = get(String, Names.named('test'))

		when:
		script.injectDependencies key, Integer

		then:
		1 * new GaelykGuiceInjector(injector, binding) >> gaelykGuiceInjector
		1 * gaelykGuiceInjector.inject(key, Integer)
	}
}

class InjectCategorySpecScript extends PluginBaseScript {
	@Override
	def run() {
	}
}