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

# ## t5/core/console
#
# A wrapper around the native console, when it exists.
define ["./dom", "./builder", "_"], (dom, builder, _) ->
  nativeConsole = {}
  floatingConsole = null

  FADE_DURATION = 0.25

  # module exports are mutable; someone else could
  # require this module to change the default DURATION
  exports =
  # Default duration for floating console is 10 seconds.
    DURATION: 10

  try
    # FireFox will throw an exception if you even access the console object and it does
    # not exist. Wow!
    nativeConsole = console
  catch e

  # _internal_: displays the message inside the floating console, creating the floating
  # console as needed.
  display = (className, message) ->
    unless floatingConsole
      floatingConsole = builder ".t-console"
      dom.body().prepend floatingConsole

    div = builder ".t-console-entry.#{className}", (dom.escapeHTML message)

    floatingConsole.append div.hide().fadeIn FADE_DURATION

    removed = false

    runFadeout = ->
      div.fadeOut FADE_DURATION, ->
        div.remove() unless removed

    window.setTimeout runFadeout, exports.DURATION * 1000

    div.on "click", ->
      div.remove()
      removed = true

  level = (className, consolefn) ->
    (message) ->
      # consolefn may be null if there's no console; under IE it may be non-null, but not a function.
      unless consolefn
        # Display it floating. If there's a real problem, such as a failed Ajax request, then the
        # client-side code should be alerting the user in some other way, and not rely on them
        # being able to see the logged console output.
        display className, message
        return

      if _.isFunction consolefn
        # Use the available native console, calling it like an instance method
        consolefn.call console, message
      else
        # And IE just has to be different. The properties of console are callable, like functions,
        # but aren't proper functions that work with `call()` either.
        consolefn message

      return


  # Determine whether debug is enabled by checking for the necessary attribute (which is missing
  # in production mode).
  exports.debugEnabled = (document.documentElement.getAttribute "data-debug-enabled")?

  # When debugging is not enabled, then the debug function becomes a no-op.
  exports.debug =
    if exports.debugEnabled
      # If native console available, go for it.  IE doesn't have debug, so we use log instead.
      level "t-debug", (nativeConsole.debug or nativeConsole.log)
    else
      ->

  exports.info = level "t-info", nativeConsole.info
  exports.warn = level "t-warn", nativeConsole.warn
  exports.error = level "t-err", nativeConsole.error

  # Return the exports; we keep a reference to it, so we can see exports.DURATION, even
  # if some other module imports this one and modifies that property.
  return exports