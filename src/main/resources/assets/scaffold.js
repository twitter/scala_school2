!function ($) {	
  $(function() {
	CodeMirror.commands.autocomplete = function(editor) {
		var Pos = CodeMirror.Pos;
		var cur = editor.getCursor(), token = editor.getTokenAt(cur), tprop = token;
	    token.state = CodeMirror.innerMode(editor.getMode(), token.state).state;
	    // If it's not a 'word-style' token, ignore the token.
	    if (!/^[\w$_]*$/.test(token.string)) {
	      token = tprop = {start: cur.ch, end: cur.ch, string: "", state: token.state,
	                       type: token.string == "." ? "property" : null};
	    }
	    // If it is a property, find out what it is a property of.
	    while (tprop.type == "property") {
	      tprop = editor.getTokenAt(Pos(cur.line, tprop.start));
	      if (tprop.string != ".") return;
	      tprop = editor.getTokenAt(Pos(cur.line, tprop.start));
	      if (tprop.string == ')') {
	        var level = 1;
	        do {
	          tprop = editor.getTokenAt(Pos(cur.line, tprop.start));
	          switch (tprop.string) {
	          case ')': level++; break;
	          case '(': level--; break;
	          default: break;
	          }
	        } while (level > 0);
	        tprop = editor.getTokenAt(Pos(cur.line, tprop.start));
	        if (tprop.type.indexOf("variable") === 0)
	          tprop.type = "function";
	        else return; // no clue
	      }
	      if (!context) var context = [];
	      context.push(tprop);
	    }
		var query = token.type == "property" ? context[0].string.concat(".") : token.string;
		$.ajax({
            type: 'POST',
            url: '/autocomplete',
            data: query,
          }).done(function (result) {
            alert(result)
			var hints = {list: result, from: Pos(cur.line, token.start), to: Pos(cur.line, token.end)};
			CodeMirror.showHint(editor, hints);
          });
	}
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
          mode: "text/x-scala",
		  extraKeys: {"Ctrl-Space": "autocomplete"}
        }),
        container = $(cm.getWrapperElement()),
        submitButton = submitButtonTemplate.clone(),
        clearButton = clearButtonTemplate.clone(),
        resetButton = resetButtonTemplate.clone(),
        buttonGroup = buttonGroupTemplate.clone().append(clearButton).append(resetButton),
        output = outputTemplate.clone().append(buttonGroup),

        submitFn = function() {	
          $.ajax({
            type: 'POST',
            url: '/',
            data: cm.getValue(),
          }).done(function (result) {
            output.removeClass('hidden').removeClass('error');
            $('div.output', output).text(result);
          }).fail(function (xhr) {
            output.removeClass('hidden').addClass('error');
            $('div.output', output).text(xhr.responseText);
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
            url: '/'
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
