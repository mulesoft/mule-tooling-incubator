$(function () {

  var
    template = Handlebars.compile($('#descriptionTemplate').html()),
    container = $('.container');

  $.getJSON('http://buds.cloudhub.io/api/updateSites/buds/plugins').then(function (result) {

    container.html(
      result.map(function (value, key) { return template(value); })
    );

  });

  container.on('click', '.thumbnail .btn-primary', function (evt) {
    var
      button = $(this),
      id = evt.target.dataset.id,
      version = evt.target.dataset.version,
      parent = button.parents('.thumbnail');

    button.button('loading');
    invoke('install->' + id + ',' + version, function (message) {
        var
          badge = parent.find('.badge'),
          current = parseInt(badge.text());
          badge.text(++current);

      $.post('http://buds.cloudhub.io/api/updateSites/buds/plugins/'+id+'/incrementInstallationCount');
      parent.addClass('installed');

      show(message);
    });

  });

});