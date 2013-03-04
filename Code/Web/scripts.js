$(function() {
  var menuActive = $('#menuSummery');

  /* Navigation bar */
  $('.nav').on('click', '#btnSummery', function() {
    toggleMenuActive($('#menuSummery'));
    var timestamp = new Date().getMilliseconds();
    var script = document.createElement('script');
    script.type = 'text/javascript';
    script.src = '/Users/Todberg/Documents/output/summery.js?' + timestamp;
    document.getElementById('content').appendChild( script );
  });

  $('.nav').on('click', '#btnCallgraph', function() {
    toggleMenuActive($('#menuCallgraph'));
    var timestamp = new Date().getMilliseconds();
    var script = document.createElement('script');
    script.type = 'text/javascript';
    script.src = '/Users/Todberg/Documents/output/callgraph.js?' + timestamp;
    document.getElementById('content').appendChild( script );
  });

  $('.nav').on('click', '#btnDetails', function() {
    toggleMenuActive($('#menuDetails'));
    var timestamp = new Date().getMilliseconds();
    var script = document.createElement('script');
    script.type = 'text/javascript';
    script.src = '/Users/Todberg/Documents/output/details.js?' + timestamp;
    document.getElementById('content').appendChild( script );  
  });

  toggleMenuActive = function(menu) {
    menuActive.removeClass('active');
    menu.addClass('active');
    menuActive = menu;
  };

  /* CFG Viewer */
  $(".pdf").fancybox({
    helpers : {
      title : {
        type : 'over'
      }
    },
    hideOnContentClick: 'false',
    type : 'iframe'
  });

  /* Style buttons */
  $("input[type=submit], a, button").button();

  $('#btnSummery').trigger('click');
});