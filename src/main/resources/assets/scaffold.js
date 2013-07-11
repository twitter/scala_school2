!function ($) {

  var
    submitButtonTemplate = $('<button class="btn btn-small btn-primary">submit</button>'),
    clearButtonTemplate = $('<button class="btn btn-mini" title="clear"><i class="icon-remove"></i></button>'),
    resetButtonTemplate = $('<button class="btn btn-mini" title="reset"><i class="icon-refresh"></i></button>'),
    buttonGroupTemplate = $('<div class="btn-group"></div>'),
    outputTemplate = $('<pre class="output hidden"><div class="output"></div></pre>'),
    interpreterCookie = 'scaffold-interpreter';

	function autoComplete(editor) {
		var Pos = CodeMirror.Pos, cur = editor.getCursor(), cur_token = editor.getTokenAt(cur);
		var tokens = editor.getLine(cur.line).split(/[^a-zA-Z0-9._]/);
		var token = tokens[tokens.length - 1];
		if (cur_token.string == ".") cur_token.start++;
		withInterpreter(function(interpreter) {
	      $.ajax({
	        type: 'POST',
	        url: interpreter + "/completions",
	        data: token,
	      }).done(function (result) {
			var hints = {list: result, from: Pos(cur.line, cur_token.start), to: Pos(cur.line, cur_token.end)};
			CodeMirror.showHint(editor, hints);
	      }).fail(function (xhr) {
	        if (xhr.status === 404) {
	        	withNewInterpreter(function (interpreter) {
	              autoComplete(editor);
	            });
	        } else {
	          console.log("Unexpected failure")
	        }
	      });
	    });
	}

  function withInterpreter(fn) {
    var interpreter = $.cookie(interpreterCookie);
    if (interpreter === undefined) {
      withNewInterpreter(fn);
    } else {
      fn(interpreter);
    }
  }

  function withNewInterpreter(fn) {
    $.ajax({
      type: 'POST',
      url: '/interpreter'
    }).done(function (_, _, xhr) {
      var interpreter = xhr.getResponseHeader('location');
      $.cookie(interpreterCookie, interpreter);
      fn(interpreter);
    }).fail(function() {
      console.log("Creating new cookie failed")
    })
  }

  function deleteInterpreter() {
    var interpreter = $.cookie(interpreterCookie);
    if (interpreter !== undefined) {
      $.removeCookie(interpreterCookie);
      $.ajax({
        type: 'DELETE',
        url: interpreter
      }).done(function () {
        var outputs = $('pre.output');
        outputs.addClass('hidden').removeClass('error');
        $('div.output', outputs).text('');
      });
    }
  }

  function evaluate(expression, output, retry) {
    withInterpreter(function(interpreter) {
      $.ajax({
        type: 'POST',
        url: interpreter,
        data: expression,
      }).done(function (result) {
        output.removeClass('hidden').removeClass('error');
        $('div.output', output).text(result);
      }).fail(function (xhr) {
        if (xhr.status === 400) {
          output.removeClass('hidden').addClass('error');
          $('div.output', output).text(xhr.responseText);
        } else if (xhr.status === 404) {
          if (retry) {
            console.log("Retry failed");
          } else {
            withNewInterpreter(function (interpreter) {
              evaluate(expression, output, true);
            });
          }
        } else {
          console.log("Unexpected failure")
        }
      });
    });
  }

  $(function() {
	CodeMirror.commands.autocomplete = autoComplete
    $('textarea').each(function (_, e) {
      var
        cm = CodeMirror.fromTextArea(e, {
          autoCloseBrackets: true,
          lineNumbers: true,
          matchBrackets: true,
          smartIndent: false,
          tabSize: 2,
          theme: "solarized light",
          mode: "text/x-scala",
		  extraKeys: {"Ctrl-Space": "autocomplete"}
        }),
        container = $(cm.getWrapperElement()),
        submitButton = submitButtonTemplate.clone(),
        clearButton = clearButtonTemplate.clone(),
        resetButton = resetButtonTemplate.clone(),
        buttonGroup = buttonGroupTemplate.clone().append(clearButton).append(resetButton),
        output = outputTemplate.clone().append(buttonGroup);

      function evaluateThis() {
        evaluate(cm.getValue(), output, false);
      }
      cm.addKeyMap({ 'Ctrl-Enter': evaluateThis });
      submitButton.click(evaluateThis);

      clearButton.click(function() {
        output.addClass('hidden').removeClass('error');
        $('div.output', output).text('');
      });

      resetButton.click(deleteInterpreter);

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

}(window.jQuery);