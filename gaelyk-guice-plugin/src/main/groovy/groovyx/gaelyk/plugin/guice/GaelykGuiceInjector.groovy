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
import groovyx.gaelyk.plugin.guice.exception.InvalidInjectionException
import com.google.inject.name.Named
import com.google.inject.Key
import static org.apache.commons.lang3.text.WordUtils.uncapitalize

class GaelykGuiceInjector {
	private final Injector injector
	private final Binding binding

	GaelykGuiceInjector(Injector injector, Binding binding) {
		this.injector = injector
		this.binding = binding
	}

	void inject(Object... args) {
		args.each { arg ->
			if (!(arg instanceof Class) && !(arg in Key)) {
				throw new InvalidInjectionException("Only classes and ${Key.name} instances are allowed " +
					'as injection specifiactions but you passed an instance of: ' +
					"'${arg.getClass().name}' with value: '$arg'")
			}
			def injectionName = getInjectionName(arg)
			verifyNoBindingFor(injectionName)
			binding[injectionName] = injector.getInstance(arg)
		}
	}

	private void verifyNoBindingFor(String injectionName) {
		if (binding.hasVariable(injectionName)) {
			throw new InvalidInjectionException("Script binding already contains a value for '$injectionName' " +
				"with class: '${binding[injectionName].getClass().name}' and value: '${binding[injectionName]}'")
		}
	}

	private String getInjectionName(Class injectedClass) {
		uncapitalize(injectedClass.simpleName)
	}

	private String getInjectionName(Key injectionKey) {
		if (injectionKey.annotation in Named) {
			injectionKey.annotation.value
		} else {
			uncapitalize(injectionKey.annotationType.simpleName)
		}
	}
}
