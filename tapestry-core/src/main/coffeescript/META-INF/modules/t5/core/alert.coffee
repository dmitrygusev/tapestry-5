# Copyright 2012 The Apache Software Foundation
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http:#www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# ## t5/core/alert
#
# Support for the core/Alerts components.
#
define ["./dom", "./console", "./messages", "./builder", "./ajax", "_"],
  (dom, console, messages, builder, ajax, _) ->

    severityToClass =
      success: "alert alert-success"
      warn: "alert alert-warning"
      error: "alert alert-error"

    getURL = (container) -> container.attribute "data-dismiss-url"

    removeAlert = (container, alert) ->
      alert.remove()

      if container.find(".alert").length is 0
        container.update null

    dismissAll = (container) ->
      console.debug "dismiss all"

      alerts = container.find "[data-alert-id]"

      if alerts.length is 0
        container.update null
        return

      ajax (getURL container),
        success: -> container.update null

    dismissOne = (container, button) ->
      console.debug "dismiss single"

      alert = button.parent()

      id = alert.attribute "data-alert-id"

      unless id
        removeAlert container, alert
        return

      ajax (getURL container),
        parameters: { id }
        success: -> removeAlert container, alert

    setupUI = (container) ->

      clickHandler = ->
        dismissAll container
        return false

      container.update builder ".well",
        ["div", "data-container-type": "inner"],
        [".row-fluid > button.btn.btn-mini.pull-right",
            onclick: clickHandler
            ["strong", "\u00d7 "],
            messages "core-dismiss-label"
        ]

      container.on "click button.close", ->
        dismissOne container, this
        return false

    findInnerContainer = ->
      outer = dom.body().findFirst "[data-container-type=alerts]"

      unless outer
        console.error "Unable to locate alert container element to present an alert."
        return null

      # Set up the inner content when needed
      unless outer.element.firstChild
        setupUI outer

      return outer?.findFirst "[data-container-type=inner]"

    # The `data` for the alert has a number of keys to control its behavior:
    #
    # * severity - used to determine the CSS class, may be "warn", "error", or "info" (the default)
    # * message - message to display to as te alert's body
    # * markup - if true, then the message contains markup that should not be HTML escaped
    alert = (data) ->

      container = findInnerContainer()

      return unless container

      className = severityToClass[data.severity] or "alert"

      content = if data.markup then data.message else dom.escapeHTML data.message

      # Note that `data-dismiss=alert` is purposely excluded
      # - we want to handle closes w/ notifications to the server if not transient
      # - we don't want to rely on bootstrap.js, as that will drag jQuery into the application
      element = builder "div", class: className,
        ["button.close", "\u00d7"]
        content

      if data.id
        element.attribute "data-alert-id", data.id

      container.append element

      if data.transient
        _.delay removeAlert, exports.TRAINSIENT_DURATION, container, element

    alert.TRAINSIENT_DURATION = 5000

    # Export the alert function
    exports = alert
