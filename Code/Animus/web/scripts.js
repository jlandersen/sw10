$(function() {
  
  /* Active items */
  var menuActive = $('#menuSummary');
  var divActive = $('#summary'); 
  var activeLiElement = $('#methods li').first(); // add or remove "active" class (submenu element)
  var activeRefDiv = null; // div holding all referenced methods with tables etc
  var activeRefSubMenu = null; // sub menu holding a list of all referenced methods
  var activeStackDiv = null;

  /* Menus */
  var menuSummary = $('#menuSummary');
  var menuCallgraph = $('#menuCallgraph');
  var menuAllocations = $('#menuAllocations');
  var menuJVMStack = $('#menuJVMStack');

  /* Content divs */
  var divContent = $('#content');
  var divSidemenuAllocations = $('#sidemenuAllocations');
  var divSidemenuJVMStack = $('#sidemenuJVMStack');

  var divSummary = $('#summary');
  var divCallgraph = $('#callgraph');
  var divAllocations = $('#allocations');
  var divJVMStack = $('#JVMStack');

  /* Heights */
  var heightSummary = divSummary.height();
  var heightCallgraph = divCallgraph.height();
  var heightAllocations = '800px';
  var heightJVMStack = '400px';

  /* Navigation bar */
  $('.nav').on('click', '#btnSummary', function() {
    if(menuActive == menuSummary)
      return;
    toggleActive(menuSummary, divSummary, heightSummary);
  });

  $('.nav').on('click', '#btnCallgraph', function() {
    if(menuActive == menuCallgraph)
      return;
    toggleActive(menuCallgraph, divCallgraph, heightCallgraph);
  });

  $('.nav').on('click', '#btnAllocations', function() {
    if(menuActive == menuAllocations) {
      return;
    } else if(menuActive == menuJVMStack) { // behold menu
      $('#sidemenuJVMStack').css('display', 'none');
      $('#sidemenuAllocations').css('display', 'block');
      divSidemenuAllocations.css({'width': '27%', 'padding': '10px'});
      toggleActiveKeepSidemenu(menuAllocations, divAllocations, heightAllocations);
    } else {
      toggleActive(menuAllocations, divAllocations, heightAllocations); // fra summary
      $('#sidemenuAllocations').css('display', 'block');
      divContent.animate({'left': '20%'}, 150, function() {
        divSidemenuAllocations.animate({'width': '27%'}, 150);
        divSidemenuAllocations.css('padding', '10px');
      });
    }  
    $('#methodsAllocations li a').first().trigger('click');
  });

  $('.nav').on('click', '#btnJVMStack', function() {
    if(menuActive == menuJVMStack) {
      return;
    } else if(menuActive == menuAllocations) { // behold menu
      $('#sidemenuAllocations').css('display', 'none');
      $('#sidemenuJVMStack').css('display', 'block');
      divSidemenuJVMStack.css({'width': '27%', 'padding': '10px'});
      toggleActiveKeepSidemenu(menuJVMStack, divJVMStack, heightJVMStack);
    } else { // fra summary
      toggleActive(menuJVMStack, divJVMStack, heightJVMStack);
      $('#sidemenuJVMStack').css('display', 'block');
      divContent.animate({'left': '20%'}, 150, function() {
          divSidemenuJVMStack.animate({'width': '27%'}, 150);
          divSidemenuJVMStack.css('padding', '10px');
      });
    }
    $('#methodsJVMStack li a').first().trigger('click');
  });

  
  toggleActive = function(menu, div, height) {
    /* Slide sidemenu back */
    if(menuActive == menuAllocations) {
      divSidemenuAllocations.animate({'width': '0%'}, 200, function() {
        divSidemenuAllocations.css('padding', '0px');
        divContent.animate({'left': '0%'}, 150);
        divSidemenuAllocations.css('display', 'none');
      });
    } else if(menuActive == menuJVMStack) {
      divSidemenuJVMStack.animate({'width': '0%'}, 200, function() {
        divSidemenuJVMStack.css('padding', '0px');
        divContent.animate({'left': '0%'}, 150);
        divSidemenuJVMStack.css('display', 'none');
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

  toggleActiveKeepSidemenu = function(menu, div, height) {
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
  }

  toggleMenuSelection = function(anchorId) {
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

  hideStack_Content = function() {
    if(activeStackDiv != null) {
      activeStackDiv.css('display', 'none');
    }
  }
  /* End: Toggle DOM elements */

  /* BEGIN: Submenu handlers */
  $('#methodsAllocations').on('click', "li a[id|='method']", function(event) {
    hideReferencedMethods_Content();

    var anchorId = event.currentTarget.id;
    toggleMenuSelection(anchorId);
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

  $('#methodsJVMStack').on('click', "li a[id|='methodjvm']", function(event) {
    hideStack_Content();

    var anchorId = event.currentTarget.id;
    toggleMenuSelection(anchorId);
    var num = anchorId.substring(10);
    
    stackId = 'stack-' + num;
    var stackDiv = $('#' + stackId);
    
    stackDiv.css('display', 'block');
    activeStackDiv = stackDiv;

  });

  $('#methodsAllocations').on('click', "li a[id|='referencedMethods']", function(event) {  
     hideCodeBoxes_Content();
     hideReferencedMethods_Content();
     hideReferencedMethods_Submenu();

     var anchorId = event.currentTarget.id;
     toggleMenuSelection(anchorId);
     var num = anchorId.substring(18);

     var refId = 'ref-' + num;
     var refDiv = $('#' + refId);

     activeRefSubMenu = $('#methodrefsub-' + num);
     $('#methodrefsub-' + num).css('display','block');
     refDiv.css('display', 'block');
     activeRefDiv = refDiv;
  });

  $('#methodsAllocations').on('click', "li a[id|='details']", function(event) {
    hideCodeBoxes_Content();
    hideReferencedMethods_Content();
    hideReferencedMethods_Submenu();

    var anchorId = event.currentTarget.id;
    toggleMenuSelection(anchorId);
    var num = anchorId.substring(8);

    var refId = 'det-' + num;
    var refDiv = $('#' + refId);

    refDiv.css('display', 'block');
    activeRefDiv = refDiv;
  });

  $('#methods').on('click', "li a[id|='methodrefsubentry']", function(event) {
    
  });
  /* END: Submenu handlers */

  $('#btnSummary').trigger('click');
});