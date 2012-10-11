# Gaelyk Guice Plugin [![Build Status](https://buildhive.cloudbees.com/job/erdi/job/gaelyk-guice-plugin/badge/icon)](https://buildhive.cloudbees.com/job/erdi/job/gaelyk-guice-plugin/)

This plugin provides support for using [Guice](http://code.google.com/p/google-guice/) as a Dependency Injection mechanism for [Gaelyk](http://gaelyk.org/) applications.

## Installation

There are several steps to install the plugin and set up your application to use Guice for Dependency Injection.

### Adding the dependency on the plugin

Plugin is available from maven central, so you need to specify that repository and add a new dependency in the Gradle build file of your project:

	repositories {
		mavenCentral()
	}

	dependencies {
		compile 'org.gaelyk:gaelyk-guice-plugin:0.1'
	}

### Modifying `web.xml`

To be able to use the plugin you need to modify `web.xml` file of your application.

#### Installing `GuiceFilter`

> **NOTE:** This step is not necessary if you do not need to use [Request or Session scopes](http://code.google.com/p/google-guice/wiki/ServletModule#Using_RequestScope) in your dependencies.

Add the following to `web.xml`.

	<filter>
		<filter-name>guiceFilter</filter-name>
		<filter-class>com.google.inject.servlet.GuiceFilter</filter-class>
	</filter>

	<filter-mapping>
		<filter-name>guiceFilter</filter-name>
		<url-pattern>/*</url-pattern>
	</filter-mapping>

#### Installing `GaelykGuiceFilter`

Add the following to `web.xml`.

	<filter>
		<filter-name>gaelykGuiceFilter</filter-name>
		<filter-class>groovyx.gaelyk.plugin.guice.GaelykGuicePluginFilter</filter-class>
	</filter>

	<filter-mapping>
		<filter-name>gaelykGuiceFilter</filter-name>
		<url-pattern>/*</url-pattern>
	</filter-mapping>

#### Extending `GuiceServletContextListener` to define dependencies

As an example let's say that we have a service class that has a String dependency:

	class MyService {
		String injected

		@Inject
		MyService(String injected) {
			this.injected = injected
		}
	}

Dependencies in Guice are resolved using an `Injector` instance. To provide one you need to extend `GuiceServletContextListener` and implement `getInjector()` method. We will therefore define how our String dependency can be resolved with `MyGuiceServletContextListener`.

	class MyGuiceServletContextListener extends GuiceServletContextListener {
		@Override
		protected Injector getInjector() {
			Guice.createInjector(new AbstractModule() {
				@Override
				protected void configure() {
					bind(String).toInstance('Hello world!')
				}
			});
		}
	}

With our dependency graph defined we can now add `MyGuiceServletContextListener` to `web.xml`:

	<listener>
		<listener-class>MyGuiceServletContextListener</listener-class>
	</listener>

## Usage

Plugin adds `injectDependencies#(Object... definitions)` method to groovlets that can be used to easily inject dependencies into them but it also provides access to the `Injector` instance created from your implementation of `GuiceServletContextListener` for more advance usages.

### Using `injectDependencies()`

After installing the plugin there is a new method available in groovlets called `injectDependencies()`. It accepts a list of injection definitions which can define injections by type, injections by name and injections by qualifier. You can provide as many definitions to one `injectDependencies()` call as you wish.

#### Injection by type

If a dependency definition passed to a `injectDependencies()` call is a class then the resolved dependency will be injected under uncapitalized class name.

Given the example service and binding specified in the [Installation section](#installation) the following code in a groovlet will pass:

	injectDependencies MyService

	assert myService.injected == 'Hello World!'

#### Injection by name

If a dependency definition passed to a `injectDependencies()` call is a `Key` instance and the binding annotation of that key is a `@Named` instance then the resolved dependency will be injected under the value of the annotation.

Given the following binding specified in your module class:

	bind(String).annotatedWith(Names.named('helloWorld')).toInstance('Hello World!')

The following code in a groovlet will pass:

	injectDependencies Key.get(String, Names.named('helloWorld'))

	assert helloWorld == 'Hello World!'

#### Injection by qualifier


### Using `Injector` directly


## Unit testing groovlets that use plugin features