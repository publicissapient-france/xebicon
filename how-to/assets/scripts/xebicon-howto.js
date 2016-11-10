var XEBICON = window.XEBICON || {};

(function (window, XEBICON) {
  XEBICON.init = function init() {
    this.document = window.document;

    // choices appearing animation
    var $appears = document.querySelectorAll('.choice.hidden');
    for (var i = 0, len = $appears.length; i < len; i++) {
      $appears[i].onload = XEBICON.appear($appears[i]);
      if (!$appears[i].classList.contains('TWITTER')) {
        $appears[i].onclick = XEBICON.showInstructions;
      }
    }
    
    // intro slide animation
    var $sliders = document.querySelectorAll('.slide-start');
    for (var i = 0, len = $sliders.length; i < len; i++) {
      $sliders[i].onload = XEBICON.slide($sliders[i]);
    }
    
    // init instructions
    XEBICON.setIsOPenInstruction();
  };
  
  XEBICON.appear = function appear(element) {
    element.classList.add('appear');
  };
  
  XEBICON.slide = function slide(element) {
    element.classList.add('slide-end');
  };
  
  XEBICON.showInstructions = function showInstructions(event) {
    event.preventDefault();
    // reset other links
    var $linkOthers = document.querySelectorAll('.choice');
    for (var i = 0, len = $linkOthers.length; i < len; i++) {
      $linkOthers[i].classList.remove('choice--selected');
    }
    // select clicked link
    var $link = event.target;
    $link.classList.add('choice--selected');
    
    // show link instruction card
    var linkId = $link.id.substr($link.id.indexOf('-') + 1, 1);

    XEBICON.setIsOPenInstruction();
    // show selected instructions
    var $instructionSelected = document.getElementById('instructions-' + linkId);
    // reset other instructions and show selected ones
    for (var i = 0, len = XEBICON.$instructionOthers.length; i < len; i++) {
      var instructionId = XEBICON.$instructionOthers[i].id.substr(XEBICON.$instructionOthers[i].id.indexOf('-') + 1, 1);
      if (XEBICON.instructionIsOpen && instructionId !== linkId) {
        XEBICON.$instructionOthers[i].classList.remove('show');
        XEBICON.$instructionOthers[i].classList.add('hide');
        XEBICON.closeInstruction(XEBICON.$instructionOthers[i], $instructionSelected);
      } else if (!XEBICON.instructionIsOpen) {
        XEBICON.showInstruction($instructionSelected);
      }
    }
  };
  
  XEBICON.setIsOPenInstruction = function isOpenInstruction() {
    XEBICON.$instructionOthers = document.querySelectorAll('.instructions');
    XEBICON.instructionIsOpen = false;
    for (var i = 0, len = XEBICON.$instructionOthers.length; i < len; i++) {
      if (!XEBICON.$instructionOthers[i].classList.contains('closed')) {
        XEBICON.instructionIsOpen = true;
      }
    }
  };
  
  XEBICON.closeInstruction = function closeInstruction(elementToClose, elementToShow) {
    setTimeout(function() {
      elementToClose.classList.add('closed');
      XEBICON.showInstruction(elementToShow)
    }, 500);
  };
  
  XEBICON.showInstruction = function showInstruction(element) {
    element.classList.remove('closed');
    element.classList.remove('hide');
    if (!XEBICON.savedTimeout) {
      XEBICON.savedTimeout = setTimeout(function() {
        element.classList.add('show');
        window.clearTimeout(XEBICON.savedTimeout);
        delete XEBICON.savedTimeout;
        // scroll after show transition is finished
        setTimeout(function() {
          var elementRect = element.getBoundingClientRect();
          var elementBottom = elementRect.bottom;
          window.scroll(0, elementBottom);
        }, 500)
      }, 10);
    }
  };
  
  window.onload = function() {
    XEBICON.init();
  };
  
  return XEBICON;
})(window, XEBICON);
