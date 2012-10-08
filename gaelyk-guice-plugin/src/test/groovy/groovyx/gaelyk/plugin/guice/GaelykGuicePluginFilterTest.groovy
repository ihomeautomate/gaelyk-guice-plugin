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

package groovyx.gaelyk.plugin.guice;


import spock.lang.Specification
import javax.servlet.ServletContext
import javax.servlet.FilterConfig
import javax.servlet.ServletRequest
import javax.servlet.FilterChain
import static groovyx.gaelyk.plugin.guice.GuicePlugin.INJECTOR_ATTRIBUTE
import com.google.inject.Injector
import javax.servlet.ServletResponse

public class GaelykGuicePluginFilterTest extends Specification {
	void "copies injector from servlet context to request"() {
		given:
		def filter = new GaelykGuicePluginFilter()
		def injector = Mock(Injector)
		def servletContext = Mock(ServletContext)
		servletContext.getAttribute(INJECTOR_ATTRIBUTE) >> injector

		def filterConfig = Mock(FilterConfig)
		filterConfig.servletContext >> servletContext

		def servletRequest = Mock(ServletRequest)
		def servletResponse = Mock(ServletResponse)
		def filterChain = Mock(FilterChain)

		when:
		filter.init(filterConfig)
		filter.doFilter(servletRequest, servletResponse, filterChain)

		then:
		1 * servletRequest.setAttribute(INJECTOR_ATTRIBUTE, injector)
		1 * filterChain.doFilter(servletRequest, servletResponse)
	}
}
