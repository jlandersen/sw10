$(function() {
  
  /* Active items */
  var menuActive = $('#menuSummary');
  var divActive = $('#summary'); 
  var activeLiElement = $('#methods li').first(); // add or remove "active" class (submenu element)
  var activeRefDiv = null; // div holding all referenced methods with tables etc
  var activeRefSubMenu = null; // sub menu holding a list of all referenced methods
  
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
    if(activeRefSubMenu != null) {
      activeRefSubMenu.css('display', 'none');
    }

    menuActive.removeClass('active');
    
    div.css('display', 'block');
    menu.addClass('active');

    menuActive = menu;
    divActive = div;
    divContent.animate({'height': height}, 250);
  };

  toggleSubmenu = function(anchorId) {
     var anchorJquery = $('#' + anchorId);
     var methodLiElement = anchorJquery.parent();

     activeLiElement.removeClass('active');
     methodLiElement.addClass('active');
     activeLiElement = methodLiElement;
  }

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

  /* BEGIN: Toggle DOM elements */
  hideCodeBoxes_Content = function() {
    $("div[id|='code']").css('display', 'none');
  }

  hideReferencedMethods_Content = function() {
    if(activeRefDiv != null)
      activeRefDiv.css('display', 'none');
  }

  hideReferencedMethods_Submenu = function() {
    if(activeRefSubMenu != null)
       activeRefSubMenu.css('display', 'none');
  }
  /* End: Toggle DOM elements */

  /* BEGIN: Submenu handlers */
  $('#methods').on('click', "li a[id|='method']", function(event) {
    hideReferencedMethods_Content();

    var anchorId = event.currentTarget.id;
    toggleSubmenu(anchorId);
    var num = anchorId.substring(7);

    // SyntaxHighlighter functionality
    var codeId = 'code-' + num;
    $('#' + codeId).css('display', 'block');
    SyntaxHighlighter.highlight(); 
    $("div[id|='code']").each(function(index, element) {
      if(element.id != codeId) {
        $('#' + element.id).css('display', 'none');
      }
    });
  });

  $('#methods').on('click', "li a[id|='referencedMethods']", function(event) {  
     hideCodeBoxes_Content();
     hideReferencedMethods_Content();
     hideReferencedMethods_Submenu();

     var anchorId = event.currentTarget.id;
     toggleSubmenu(anchorId);
     var num = anchorId.substring(18);

     var refId = 'ref-' + num;
     var refDiv = $('#' + refId);

     activeRefSubMenu = $('#methodrefsub-' + num);
     $('#methodrefsub-' + num).css('display','block');
     refDiv.css('display', 'block');
     activeRefDiv = refDiv;
  });

  $('#methods').on('click', "li a[id|='methodrefsubentry']", function(event) {
    
  });
  /* END: Submenu handlers */

  $('#btnSummary').trigger('click');
});