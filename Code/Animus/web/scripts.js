$(function() {
  
  /* Active items */
  var menuActive = $('#menuSummary');
  var divActive = $('#summary'); 
  var activeLiElement = $('#methods li').first(); // add or remove "active" class
  var activeRefDiv = null;
  
  /* Menus */
  var menuSummary = $('#menuSummary');
  var menuCallgraph = $('#menuCallgraph');
  var menuDetails = $('#menuDetails');

  /* Content divs */
  var divContent = $('#content');
  var divSidemenu = $('#sidemenu');
  var divSummary = $('#summary');
  var divCallgraph = $('#callgraph');
  var divDetails = $('#details');

  /* Heights */
  var heightSummary = divSummary.height();
  var heightCallgraph = divCallgraph.height();
  var heightDetails = '800px';

  /* Navigation bar */
  $('.nav').on('click', '#btnSummary', function() {
    toggleActive(menuSummary, divSummary, heightSummary);
    $('#detailsPanel').slideUp();
  });

  $('.nav').on('click', '#btnCallgraph', function() {
    toggleActive(menuCallgraph, divCallgraph, heightCallgraph);
    $('#detailsPanel').slideUp();
  });

  $('.nav').on('click', '#btnDetails', function() {
    if(menuActive == menuDetails)
      return;
    toggleActive(menuDetails, divDetails, heightDetails);
    $('#detailsPanel').slideDown();
    divContent.animate({'left': '20%'}, 150, function() {
      divSidemenu.animate({'width': '27%'}, 150);
      divSidemenu.css('padding', '10px');
    });
    $('#methods li a').first().trigger('click');
  });
  
  toggleActive = function(menu, div, height) {
    /* Slide sidemenu back */
    if(menuActive == menuDetails) {
      divSidemenu.animate({'width': '0%'}, 200, function() {
        divSidemenu.css('padding', '0px');
        divContent.animate({'left': '0%'}, 150);
      });
    }
    divActive.css('display', 'none'); // hide active div

    menuActive.removeClass('active');
    
    div.css('display', 'block');
    menu.addClass('active');

    menuActive = menu;
    divActive = div;
    divContent.animate({'height': height}, 250);
  };

  /* CFG Viewer */  
  $(".cfgViewer").fancybox({
    fitToView : false,
    width   : '95%',
    height    : '95%',
    autoSize  : false,
    closeClick  : false,
    openEffect  : 'fade',
    closeEffect : 'elastic'
  });

  $('#methods').on('click', "li a[id|='method']", function(event) {
    var method = event.currentTarget; // DOM element (anchor)
    var methodId = event.currentTarget.id // "method-x"
    var methodJquery = $('#' + methodId); // Jquery object
    var methodLiElement = methodJquery.parent();

    /* Switch between actives */
    activeLiElement.removeClass('active');
    methodLiElement.addClass('active');
    activeLiElement = methodLiElement;

    var num = methodId.substring(7) // xxxxxx-xxxxx (GUID)

    var codeId = 'code-' + num;
    $('#' + codeId).css('display', 'block');
    SyntaxHighlighter.highlight(); 
    $("div[id|='code']").each(function(index, element) {
      if(element.id != codeId) {
        $('#' + element.id).css('display', 'none');
      }
    });
  });

  $('#methods').on('click', "li a[id|='methodref']", function(event) {
     $("div[id|='code']").css('display', 'none');
     if(activeRefDiv != null)
        activeRefDiv.css('display', 'none');
     
     var anchor = event.currentTarget;
     var anchorId = event.currentTarget.id;
     var anchorJquery = $('#' + anchorId);
     var anchorLiElement = anchorJquery.parent();

     activeLiElement.removeClass('active');
     anchorLiElement.addClass('active');
     activeLiElement = anchorLiElement;

     var num = anchorId.substring(10) // xxxxxx-xxxxx (GUID)
     var refId = 'ref-' + num;
     var refDiv = $('#' + refId);

     refDiv.css('display', 'block');
     activeRefDiv = refDiv;
  });

  $('#btnSummary').trigger('click');
});