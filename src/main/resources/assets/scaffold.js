!function ($) {
  $(function() {
    //// Session management

    // Global session vars
    var posturl = ""
    var cookiename = 'location'

    // Session helper functions
    newSession = function(successCallback) {
      var cookie = $.cookie(cookiename)
      if (cookie === undefined) {
        createCookie(successCallback)
      } else {
        posturl = cookie  // /console/id
      }
    }

    createCookie = function(successCallback) {
      $.ajax({
        type: 'POST',
        url: '/console'
      }).success(function(data, textStatus, request){
        $.cookie(cookiename, request.getResponseHeader('location'), { expires: 7 })
        posturl = request.getResponseHeader('location')
        if (successCallback !== undefined) {
          successCallback()
        }
      }).fail(function() {
        console.log("Creating new cookie failed")
      })
    }

    // Initializers
    newSession()  // call on startup to set posturl var


    //// Interpreter functionality

    var
      submitButtonTemplate = $('<button class="btn btn-small btn-primary">submit</button>'),
      clearButtonTemplate = $('<button class="btn btn-mini" title="clear"><i class="icon-remove"></i></button>'),
      resetButtonTemplate = $('<button class="btn btn-mini" title="reset"><i class="icon-refresh"></i></button>'),
      buttonGroupTemplate = $('<div class="btn-group"></div>'),
      outputTemplate = $('<pre class="output hidden"><div class="output"></div></pre>');

    $('textarea').each(function (_, e) {
      var
        cm = CodeMirror.fromTextArea(e, {
          autoCloseBrackets: true,
          lineNumbers: true,
          matchBrackets: true,
          smartIndent: false,
          tabSize: 2,
          theme: "solarized light",
          mode: "text/x-scala"
        }),
        container = $(cm.getWrapperElement()),
        submitButton = submitButtonTemplate.clone(),
        clearButton = clearButtonTemplate.clone(),
        resetButton = resetButtonTemplate.clone(),
        buttonGroup = buttonGroupTemplate.clone().append(clearButton).append(resetButton),
        output = outputTemplate.clone().append(buttonGroup),

        // Submit code to console interpreter
        submitFn = function() {
          $.ajax({
            type: 'POST',
            url: posturl,
            data: cm.getValue(),
          }).done(function (result) {
            output.removeClass('hidden').removeClass('error');
            $('div.output', output).text(result);
          }).fail(function (xhr) {
            if (xhr.status === 404) {  // bad cookie
              // recreate cookie and try submission again
              createCookie(function() {
                $.ajax({
                  type: 'POST',
                  url: posturl,
                  data: cm.getValue(),
                }).done(function (result) {
                  output.removeClass('hidden').removeClass('error');
                  $('div.output', output).text(result)
                }).fail(function (xhr){
                  output.removeClass('hidden').addClass('error');
                  $('div.output', output).text(xhr.responseText);
                })
              })
            } else {  // other error
              output.removeClass('hidden').addClass('error');
              $('div.output', output).text(xhr.responseText);
            }
          });
        };

        cm.addKeyMap({ 'Ctrl-Enter': submitFn });
        submitButton.click(submitFn);

        clearButton.click(function() {
          output.addClass('hidden').removeClass('error');
          $('div.output', output).text('');
        });

        resetButton.click(function() {
          $.ajax({
            type: 'DELETE',
            url: posturl
          }).done(function () {
            var outputs = $('pre.output');
            outputs.addClass('hidden').removeClass('error');
            $('div.output', outputs).text('');
          });
        });

      container.append(submitButton);
      container.after(output);
    });

    $('.scaffold-sidenav').affix({
      offset: {
        top: function() { return $(window).width() <= 980 ? 290 : 210 },
        bottom: 270
      }
    });
  });

}(window.jQuery)
