!function ($) {
  $(function() {

    var
      submitButtonTemplate = $('<button class="btn btn-small btn-primary">submit</button>'),
      resetButtonTemplate = $('<button class="btn btn-small btn-danger">reset</button>'),
      outputTemplate = $('<pre class="output"><div></div></pre>');

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
        resetButton = resetButtonTemplate.clone(),
        output = outputTemplate.clone().append(resetButton);

        submitButton.click(function() {
          $.ajax({
            type: 'POST',
            url: 'http://localhost:8080',
            data: cm.getValue(),
          }).done(function (result) {
            output.removeClass('error');
            $('div', output).text(result);
          }).fail(function (xhr) {
            output.addClass('error');
            $('div', output).text(xhr.responseText);
          });
        });

        resetButton.click(function() {
          $.ajax({
            type: 'DELETE',
            url: 'http://localhost:8080'
          }).done(function () {
            var outputs = $('.output');
            outputs.removeClass('error');
            $('div', outputs).text('');
          })
        })
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
