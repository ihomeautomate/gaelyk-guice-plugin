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

class GaelykGuiceInjector {
	private final injector
	private final binding

	GaelykGuiceInjector(Injector injector, Binding binding) {
		this.injector = injector
		this.binding = binding
	}

	void inject(Object... args) {
		args.each { arg ->
			if (arg in Key) {
				if (arg.annotationType != Named) {
					throw new InvalidInjectionException("Unsupported injection key annotation: '${arg.annotationType.name}'" +
							", only '${Named.name}' is supported as key annotation")
				}
			}
		}
	}
}
