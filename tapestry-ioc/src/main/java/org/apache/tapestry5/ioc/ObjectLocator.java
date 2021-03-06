// Copyright 2006, 2007, 2010, 2011 The Apache Software Foundation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.apache.tapestry5.ioc;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.services.MasterObjectProvider;

import java.lang.annotation.Annotation;

/**
 * Defines an object which can provide access to services defined within a {@link org.apache.tapestry5.ioc.Registry}, or
 * to objects or object instances available by other means. Services are accessed via service id, or
 * (when appropriate)
 * by just service interface. The Registry itself implements this interface, as does
 * {@link org.apache.tapestry5.ioc.ServiceResources}.
 */
public interface ObjectLocator
{
    /**
     * Obtains a service via its unique service id. Returns the service's proxy. The service proxy
     * implements the same
     * interface as the actual service, and is used to instantiate the actual service only as needed
     * (this is
     * transparent to the application).
     *
     * @param <T>
     * @param serviceId        unique Service id used to locate the service object (may contain <em>symbols</em>,
     *                         which
     *                         will be expanded), case is ignored
     * @param serviceInterface the interface implemented by the service (or an interface extended by the service
     *                         interface)
     * @return the service instance
     * @throws RuntimeException if the service is not defined, or if an error occurs instantiating it
     */
    <T> T getService(String serviceId, Class<T> serviceInterface);

    /**
     * Locates a service given a service interface and (optionally) some marker annotation types. A single service must implement the service
     * interface (which                                                   * can be hard to guarantee) and by marked by all the marker types. The search takes into account inheritance of the service interface
     * (not the service <em>implementation</em>), which may result in a failure due to extra
     * matches.
     *
     * @param serviceInterface the interface the service implements
     * @return the service's proxy
     * @throws RuntimeException if the service does not exist (this is considered programmer error), or multiple
     *                          services directly implement, or extend from, the service interface
     * @see org.apache.tapestry5.ioc.annotations.Marker
     */
    <T> T getService(Class<T> serviceInterface);

    /**
     * Locates a service given a service interface and (optionally) some marker annotation types. A single service must implement the service
     * interface (which                                                   * can be hard to guarantee) and by marked by all the marker types. The search takes into account inheritance of the service interface
     * (not the service <em>implementation</em>), which may result in a failure due to extra
     * matches.        The ability to specify marker annotation types was added in 5.3
     *
     * @param serviceInterface the interface the service implements
     * @param markerTypes      Markers used to select a specific service that implements the interface
     * @return the service's proxy
     * @throws RuntimeException if the service does not exist (this is considered programmer error), or multiple
     *                          services directly implement, or extend from, the service interface
     * @see org.apache.tapestry5.ioc.annotations.Marker
     * @since 5.3
     */
    <T> T getService(Class<T> serviceInterface, Class<? extends Annotation>... markerTypes);

    /**
     * Obtains an object indirectly, using the {@link org.apache.tapestry5.ioc.services.MasterObjectProvider} service.
     *
     * @param objectType         the type of object to be returned
     * @param annotationProvider provides access to annotations on the field or parameter for which a value is to
     *                           be
     *                           obtained, which may be utilized in selecting an appropriate object, use
     *                           <strong>null</strong> when annotations are not available (in which case, selection
     *                           will
     *                           be based only on the object type)
     * @param <T>
     * @return the requested object
     * @see ObjectProvider
     */
    <T> T getObject(Class<T> objectType, AnnotationProvider annotationProvider);

    /**
     * Autobuilds a class by finding the public constructor with the most parameters. Services and other resources or
     * dependencies will be injected into the parameters of the constructor and into private fields marked with the
     * {@link Inject} annotation. There are two cases: constructing a service implementation, and constructing
     * an arbitrary object. In the former case, many <em>service resources</em> are also available for injection, not
     * just dependencies or objects provided via
     * {@link MasterObjectProvider#provide(Class, AnnotationProvider, ObjectLocator, boolean)}.
     *
     * @param <T>
     * @param clazz the type of object to instantiate
     * @return the instantiated instance
     * @throws RuntimeException if the autobuild fails
     * @see MasterObjectProvider
     */
    <T> T autobuild(Class<T> clazz);

    /**
     * Preferred version of {@link #autobuild(Class)} that tracks the operation using
     * {@link OperationTracker#invoke(String, Invokable)}.
     *
     * @param <T>
     * @param description description used with {@link OperationTracker}
     * @param clazz       the type of object to instantiate
     * @return the instantiated instance
     * @throws RuntimeException if the autobuild fails
     * @see MasterObjectProvider
     * @since 5.2.0
     */
    <T> T autobuild(String description, Class<T> clazz);

    /**
     * Creates a proxy. The proxy will defer invocation of {@link #autobuild(Class)} until
     * just-in-time (that is, first method invocation). In a limited number of cases, it is necessary to use such a
     * proxy to prevent service construction cycles, particularly when contributing (directly or indirectly) to the
     * {@link org.apache.tapestry5.ioc.services.MasterObjectProvider} (which is itself at the heart
     * of autobuilding).
     * <p/>
     * If the class file for the class is a file on the file system (not a file packaged in a JAR), then the proxy will
     * <em>autoreload</em>: changing the class file will result in the new class being reloaded and re-instantiated
     * (with dependencies).
     *
     * @param <T>
     * @param interfaceClass      the interface implemented by the proxy
     * @param implementationClass a concrete class that implements the interface
     * @return a proxy
     * @see #autobuild(Class)
     */
    <T> T proxy(Class<T> interfaceClass, Class<? extends T> implementationClass);
}
