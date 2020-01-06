(function($) {
  "use strict";

  var docsearchInputId = '#docsearch';

  docsearch({
   apiKey: '684751d463a8f3cac9bc4b2d8740642c',
   indexName: 'arrow_meta',
   inputSelector: docsearchInputId,
   debug: false // Set debug to true if you want to inspect the dropdown
  });

  $(document).ready(function() {

    // To focus the searchbar on load. Autofocus won't work since, in the end,
    // the input is injected externally by the Algolia autocomplete.js library.
    // There is native way to focus an element without scrolling the view into it,
    // but it's not possible to detect current engine's support in a clean way (yet).
    // https://github.com/heycam/webidl/issues/107#issuecomment-399910305
    var actualPosition = window.scrollY;
    document.querySelector(docsearchInputId).focus();
    // Hijacking the event loop order, since the focus() will trigger
    // internally an scroll that goes to the event loop
    setTimeout(function() {
      window.scroll(window.scrollX, actualPosition);
    }, 0);

    // Following functions are related to the sidebar nav
    // Show and hide the sidebar
    $(".sidebar-toggle").click(function(e) {
      e.preventDefault();
      $("#wrapper").toggleClass("toggled");
    });

    // Show and hide the sidebar
    $(".button-video").click(function(e) {
      e.preventDefault();
      $("#video-panel").slideToggle("toggled");
    });

    var anchorForId = function(id) {
      var anchor = document.createElement("a");
      anchor.className = "header-link";
      anchor.href = "#" + id;
      anchor.innerHTML = "<i class=\"fa fa-link\"></i>";
      return anchor;
    };

    var linkifyAnchors = function(level, containingElement) {
      var headers = containingElement.getElementsByTagName("h" + level);
      for (var h = 0; h < headers.length; h++) {
        var header = headers[h];

        if (typeof header.id !== "undefined" && header.id !== "") {
          header.appendChild(anchorForId(header.id));
        }
      }
    };

    var linkifyAllLevels = function(blockSelector) {
      var contentBlock = document.querySelector(blockSelector);
      if (!contentBlock) {
        return;
      }
      for (var level = 1; level <= 6; level++) {
        linkifyAnchors(level, contentBlock);
      }
    };

    linkifyAllLevels(".doc-content, .blog-content");

    /**
     * This function generates the “unrolling” of the module dropdown
     * secction by adding some classes to the element and applying a
     * jQuery slide action
     *
     * @param el The DOM element on which to perform the action
     * @param speed The desired speed to slide up/down the section
     */
    function activate(el, speed) {
      if (!el.parent().hasClass('active')) {
        $('.sidebar-nav li ul').slideUp(speed);
        el.next().slideToggle(speed);
        $('.sidebar-nav li').removeClass('active');
        el.parent().addClass('active');
      } else {
        el.next().slideToggle(speed);
        $('.sidebar-nav li').removeClass('active');
      }
    }

    // On click slide down or up the module dropdown
    $('.cat-dropdown').click(function(e) {
      e.preventDefault();
      activate($(this), 300);
    });

  });
})(jQuery);

/**
 * Remove active class from siblings DOM elements and apply it to event target.
 * @param {Element}		element The element receiving the class, and whose siblings will lose it.
 * @param {string}		[activeClass='active'] The class to be applied.
 */
function activate(element, activeClass = 'active') {
  [...element.parentNode.children].map((elem) => elem.classList.remove(activeClass));
  element.classList.add(activeClass);
}

/**
 * Remove active class from siblings parent DOM elements and apply it to element target parent.
 * @param {Element}		element The element receiving the class, and whose siblings will lose it.
 * @param {string}		[activeClass='active'] The class to be applied.
 */
function activateParent(element, activeClass = 'active') {
  const elemParent = element.parentNode;
  activate(elemParent, activeClass);
}
