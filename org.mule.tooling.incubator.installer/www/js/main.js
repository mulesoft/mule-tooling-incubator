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
      id = evt.target.dataset.id,
      version = evt.target.dataset.version;

    invoke('install->' + id + ',' + version, show);

  });

});