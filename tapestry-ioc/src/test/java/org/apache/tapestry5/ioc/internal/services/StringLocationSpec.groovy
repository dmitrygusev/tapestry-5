package org.apache.tapestry5.ioc.internal.services

import org.apache.tapestry5.ioc.Location
import spock.lang.Specification

class StringLocationSpec extends Specification {

  def "access to properties"() {
    def description = "location description", line = 909

    when:
    Location l = new StringLocation(description, line)

    then:

    l.toString().is(description)
    l.line == line
    l.column == 0
    l.resource == null
  }
}
