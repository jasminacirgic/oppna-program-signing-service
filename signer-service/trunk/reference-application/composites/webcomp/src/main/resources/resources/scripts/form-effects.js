$(document).ready(function() {
  $form = $('form#sign-selection');
  
  //Replace radio buttons and label with a submit link
  $('input[name=clientType]:radio', $form).each(function(i, radio) {
    var $radio = $(radio);
    var $label = $('label[for=' + $radio.attr('id') + ']')
    $('<a></a>', {href: '#'}).append($label.text()).click(function() {
      $('<input/>', {type: 'hidden', value: $radio.val(), id: $radio.attr('name'), name: $radio.attr('name')}).appendTo($form);
      $form.submit();
      return false;
    }).insertBefore($radio);
    $label.remove();
    $radio.remove();
  });
  
  $('input:submit', $form).remove();
});
