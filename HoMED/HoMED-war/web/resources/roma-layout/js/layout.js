/** 
 * PrimeFaces Roma Layout
 */
PrimeFaces.widget.Roma = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        this.wrapper = $(document.body).children('.layout-wrapper');
        this.topbar = this.wrapper.find('.layout-topbar');
        this.menuButton = this.topbar.find('.layout-menu-button');
        this.menuContainer = this.wrapper.find('.layout-menu-container');
        this.menu = this.jq;
        this.menulinks = this.menu.find('a');
        this.nanoContainer = this.menuContainer.find('.nano');
        this.nanoScrollContent = this.nanoContainer.children('.nano-content');
        this.expandedMenuitems = this.expandedMenuitems||[];

        this.topbarMenu = this.topbar.children('.topbar-menu');
        this.topbarItems = this.topbarMenu.children('li');
        this.topbarLinks = this.topbarItems.children('a');

        var isSlimMenu = this.wrapper.hasClass('layout-slim');
        var isHorizontalMenu = this.wrapper.hasClass('layout-horizontal');

        this.profileMenuClick = false;
        this.topbarMenuClick = false;
        this.rightPanelClick = false;

        this.profileContainer = $('.layout-profile');
        this.profileButton = this.profileContainer.children('.layout-profile-button');
        this.profileMenu = this.profileContainer.children('.layout-profile-menu');

        this.configButton = $('#layout-config-button');
        this.configMenu = $('#layout-config');
        this.configMenuClose = this.configMenu.find('> .layout-config-content > .layout-config-close');

        if(!(isSlimMenu && this.isDesktop()) && !(isHorizontalMenu && this.isDesktop())) {
            this.restoreMenuState();
        }
        
        if(!isHorizontalMenu) {
            this.initNanoScroll();
        }

        this._bindEvents();
    },
    
    initNanoScroll: function() {       
        this.nanoContainer.nanoScroller({ flash: true, isRTL: this.wrapper.hasClass('layout-rtl') });
        this.nanoScrollContent.removeAttr('tabindex');

        if(this.cfg.statefulScroll) {
            var $this = this,
            scrollTopVal = $.cookie('roma_scroll');
            if (scrollTopVal) {
                setTimeout(function() {
                    $this.nanoScrollContent.scrollTop(scrollTopVal);
                }, 10);
            }

            this.nanoContainer.on("update", function(event, values){
                clearTimeout($this.scrollTimeout);
                $this.scrollTimeout = setTimeout(function() {
                    $this.saveScrollState($this.nanoScrollContent.scrollTop());
                }, 250);
            });
        }
    },

    _bindEvents: function() {
        var $this = this;

        $this.menuContainer.off('click.menu').on('click.menu', function() {
            $this.menuClick = true;
        });

        this.menuButton.off('click.menu').on('click.menu', function(e) {
            $this.menuClick = true;

            if($this.isMobile()) {
                $this.wrapper.toggleClass('layout-mobile-active');
                $(document.body).toggleClass('blocked-scroll');
                
                setTimeout(function () {
                    $this.nanoContainer.nanoScroller({isRTL: $this.wrapper.hasClass('layout-rtl')});
                    $this.nanoScrollContent.removeAttr('tabindex');
                }, 450);
            }
            else {
                if($this.isStaticMenu()) {
                    $this.wrapper.toggleClass('layout-static-inactive');
                    $this.saveStaticMenuState();
                }
                else {
                    $this.wrapper.toggleClass('layout-overlay-active');
                }               
            }

            e.preventDefault();
        });

        $this.profileButton.off('click.profile').on('click.profile', function (e) {
            if (!$this.isHorizontalMenu() && !$this.isSlimMenu()) {
                $this.profileContainer.toggleClass('layout-profile-active');
                $this.profileMenu.slideToggle();

                setTimeout(function () {
                    $this.nanoContainer.nanoScroller();
                    $this.nanoScrollContent.removeAttr('tabindex');
                }, 500);
            }
            else {
                $this.profileMenuClick = true;

                if ($this.profileContainer.hasClass('layout-profile-active')) {
                    $this.profileMenu.addClass('fadeOutUp');

                    setTimeout(function () {
                        $this.profileContainer.removeClass('layout-profile-active');
                        $this.profileMenu.removeClass('fadeOutUp');
                    }, 150);
                }
                else {
                    $this.profileContainer.addClass('layout-profile-active');
                    $this.profileMenu.addClass('fadeInDown');
                }
            }

            e.preventDefault();
        });

        this.menulinks.off('click.menu').on('click.menu', function (e) {
            var link = $(this),
            item = link.parent('li'),
            submenu = item.children('ul');

            if (item.hasClass('active-menuitem')) {
                if (submenu.length) {
                    $this.removeMenuitem(item.attr('id'));
                    
                    if($this.isSlimMenu() || $this.isHorizontalMenu()) {
                        item.removeClass('active-menuitem');
                        submenu.hide();
                    }
                    else {
                        submenu.slideUp(400, function() {
                            item.removeClass('active-menuitem');
                        });
                    }
                }

                if(item.parent().is($this.jq)) {
                    $this.menuActive = false;
                }
            }
            else {
                $this.addMenuitem(item.attr('id'));

                if($this.isSlimMenu() || $this.isHorizontalMenu()) {
                    $this.deactivateItems(item.siblings(), false);

                    if(submenu.length === 0) {
                        $this.resetMenu();
                    }
                }
                else {
                    $this.deactivateItems(item.siblings(), true);
                }
                
                $this.activate(item);
                
                if(item.parent().is($this.jq)) {
                    $this.menuActive = true;
                }
            }
            
            setTimeout(function() {
                $this.nanoContainer.nanoScroller();
                $this.nanoScrollContent.removeAttr('tabindex');
            }, 450);

            if (submenu.length) {
                e.preventDefault();
            }
        });

        this.menu.children('li').off('mouseenter.menu').on('mouseenter.menu', function(e) {    
            if($this.isHorizontalMenu() || $this.isSlimMenu()) {
                var item = $(this);
                
                if(!item.hasClass('active-menuitem')) {
                    $this.menu.find('.active-menuitem').removeClass('active-menuitem');
                    $this.menu.find('ul:visible').hide();
                    
                    if($this.menuActive) {
                        item.addClass('active-menuitem');
                        item.children('ul').show();
                    }
                }
            }
        });

        this.topbarLinks.off('click.topbar').on('click.topbar', function (e) {
            var link = $(this),
                item = link.parent(),
                submenu = link.next();

            $this.topbarMenuClick = true;

            item.siblings('.active-topmenuitem').removeClass('active-topmenuitem');

            if ($this.isDesktop()) {
                if (submenu.length) {
                    if (item.hasClass('active-topmenuitem')) {
                        $this.hideTopBarSubMenu(item);
                    }
                    else {
                        item.addClass('active-topmenuitem');
                        submenu.addClass('fadeInDown');
                    }
                }
            }
            else {
                item.children('ul').removeClass('fadeInDown fadeOutUp');
                item.toggleClass('active-topmenuitem');
            }

            if (link.attr('href') === '#') {
                e.preventDefault();
            }
        });

        this.bindConfigEvents();

        $(document.body).off('click.layoutBody').on('click.layoutBody', function() {
            if(($this.isHorizontalMenu() || $this.isSlimMenu()) && !$this.menuClick && $this.isDesktop()) {
                $this.menu.find('.active-menuitem').removeClass('active-menuitem');
                $this.menu.find('ul:visible').hide();
                $this.menuActive = false;
            }

            if (!$this.topbarMenuClick) {
                $this.hideTopBarSubMenu($this.topbarItems.filter('.active-topmenuitem'));
            }

            if (!$this.rightPanelClick && $this.rightPanel && $this.rightPanel.hasClass('layout-right-panel-active')) {
                $this.rightPanel.removeClass('layout-right-panel-active')
            }

            if(!$this.menuClick) {
                $this.wrapper.removeClass('layout-overlay-active layout-mobile-active');
                $(document.body).removeClass('blocked-scroll');
            }

            if (!$this.profileMenuClick && ($this.isHorizontalMenu() || $this.isSlimMenu())) {
                $this.profileContainer.removeClass('layout-profile-active');
            }

            if (!$this.configMenuClicked) {
                $this.configMenu.removeClass('layout-config-active');
            }

            $this.menuClick = false;
            $this.topbarMenuClick = false;
            $this.rightPanelClick = false;
            $this.profileMenuClick = false;
            $this.configMenuClicked = false;
        });

        $(function() {
            $this._initrightPanel();
        });
    },

    hideTopBarSubMenu: function(item) {
        var submenu = item.children('ul');
        submenu.addClass('fadeOutUp');

        setTimeout(function () {
            item.removeClass('active-topmenuitem'),
                submenu.removeClass('fadeOutUp');
        }, 150);
    },

    bindConfigEvents: function() {
        var $this = this;
        var changeConfigMenuState = function(e) {
            this.toggleClass(this.configMenu, 'layout-config-active');
            
            this.configMenuClicked = true;
            e.preventDefault();
        };
        
        this.configButton.off('click.config').on('click.config', changeConfigMenuState.bind(this));
        this.configMenuClose.off('click.config').on('click.config', changeConfigMenuState.bind(this));
        
        this.configMenu.off('click.configMenu').on('click.configMenu', function() {
            $this.configMenuClicked = true;
        });
    },

    resetMenu: function() {
        this.menu.find('.active-menuitem').removeClass('active-menuitem');
        this.menu.find('ul:visible').hide();
        this.menuActive = false;
    },

    activate: function(item) {
        var submenu = item.children('ul');
        item.addClass('active-menuitem');

        if(submenu.length) {
            if(this.isSlimMenu() || this.isHorizontalMenu())
                submenu.show();
            else
                submenu.slideDown();
        }
    },

    deactivate: function(item) {
        var submenu = item.children('ul');
        item.removeClass('active-menuitem');

        if(submenu.length) {
            submenu.hide();
        }
    },

    deactivateItems: function(items, animate) {
        var $this = this;

        for(var i = 0; i < items.length; i++) {
            var item = items.eq(i),
            submenu = item.children('ul');

            if(submenu.length) {
                if(item.hasClass('active-menuitem')) {
                    var activeSubItems = item.find('.active-menuitem');
                    item.removeClass('active-menuitem');

                    if(animate) {
                        submenu.slideUp('normal', function() {
                            $(this).parent().find('.active-menuitem').each(function() {
                                $this.deactivate($(this));
                            });
                        });
                    }
                    else {
                        submenu.hide();
                        item.find('.active-menuitem').each(function() {
                            $this.deactivate($(this));
                        });
                    }

                    $this.removeMenuitem(item.attr('id'));
                    activeSubItems.each(function() {
                        $this.removeMenuitem($(this).attr('id'));
                    });
                }
                else {
                    item.find('.active-menuitem').each(function() {
                        var subItem = $(this);
                        $this.deactivate(subItem);
                        $this.removeMenuitem(subItem.attr('id'));
                    });
                }
            }
            else if(item.hasClass('active-menuitem')) {
                $this.deactivate(item);
                $this.removeMenuitem(item.attr('id'));
            }
        }
    },

    removeMenuitem: function (id) {
        this.expandedMenuitems = $.grep(this.expandedMenuitems, function (value) {
            return value !== id;
        });

        this.saveMenuState();
    },

    addMenuitem: function (id) {
        if ($.inArray(id, this.expandedMenuitems) === -1) {
            this.expandedMenuitems.push(id);
        }
        this.saveMenuState();
    },

    saveMenuState: function() {
        $.cookie('roma_expandeditems', this.expandedMenuitems.join(','), {path: '/'});
    },
    
    saveScrollState: function (value) {
        $.cookie('roma_scroll', value, { path: '/' });
    },

    clearMenuState: function() {
        this.expandedMenuitems = [];
        $.removeCookie('roma_expandeditems', {path: '/'});
        $.removeCookie('roma_scroll', { path:'/' });
    },

    saveStaticMenuState: function() {
        if(this.wrapper.hasClass('layout-static-inactive'))
            $.cookie('roma_static_menu_inactive', 'roma_static_menu_inactive', {path: '/'});
        else
            $.removeCookie('roma_static_menu_inactive', {path: '/'});
    },

    isMobile: function( ){
        return $(window).width() <= 896;
    },
    
    isStaticMenu: function() {
        return this.wrapper.hasClass('layout-static') && this.isDesktop();
    },

    isHorizontalMenu: function() {
        return this.wrapper.hasClass('layout-horizontal') && this.isDesktop();
    },
    
    isSlimMenu: function() {
        return this.isDesktop() && this.wrapper.hasClass('layout-slim');
    },

    isDesktop: function() {
        return window.innerWidth > 896;
    },

    restoreMenuState: function() {
        var menucookie = $.cookie('roma_expandeditems');
        if (menucookie) {
            this.expandedMenuitems = menucookie.split(',');
            for (var i = 0; i < this.expandedMenuitems.length; i++) {
                var id = this.expandedMenuitems[i];
                if (id) {
                    var menuitem = $("#" + this.expandedMenuitems[i].replace(/:/g, "\\:"));
                    menuitem.addClass('active-menuitem');

                    var submenu = menuitem.children('ul');
                    if(submenu.length) {
                        submenu.show();
                    }
                }
            }
        }

        var staticMenuCookie = $.cookie('roma_static_menu_inactive');
        if(staticMenuCookie) {
            this.wrapper.addClass('layout-static-inactive layout-static-inactive-restore');
        }
    },

    clearActiveItems: function() {
        var activeItems = this.jq.find('li.active-menuitem'),
        subContainers = activeItems.children('ul');

        activeItems.removeClass('active-menuitem');
        if(subContainers && subContainers.length) {
            subContainers.hide();
        }
    },

    clearLayoutState: function() {
        this.clearMenuState();
        this.clearActiveItems();
    },

    _initrightPanel: function() {
        var $this = this;

        this.rightPanelButton = $('.layout-right-panel-button');
        this.rightPanel = $('.layout-right-panel');

        this.rightPanelButton.off('click').on('click', function(e) {
            $this.rightPanel.toggleClass('layout-right-panel-active');
            $this.rightPanelClick = true;
            e.preventDefault();
        });

        this.rightPanel.off('click').on('click', function (e) {
            $this.rightPanelClick = true;

            var target = e.target;
            if (!(target.nodeName === "A" && target.getAttribute('href') !== '#')) {
                e.preventDefault();
            }
        });
    },
    toggleClass: function(el, className) {
        if (el.hasClass(className)) {
            el.removeClass(className);
        }
        else {
            el.addClass(className);
        }
    },
    
    closerightPanelMenu: function() {
        if(this.rightPanel) {
            this.rightPanel.removeClass('layout-right-panel-active');
        }
    }
});

PrimeFaces.RomaConfigurator = {
    changeLayout: function(layoutTheme) {
        var linkElement = $('link[href*="layout-"');
        var href = linkElement.attr('href');
        var startIndexOf = href.indexOf('layout-') + 7;
        var endIndexOf = href.indexOf('.css');
        var currentColor = href.substring(startIndexOf, endIndexOf);

        this.replaceLink(linkElement, href.replace(currentColor, layoutTheme));
    },

    changeComponentsTheme: function(theme) {
        var library = 'primefaces-roma';
        var linkElement = $('link[href*="theme.css"]');
        var href = linkElement.attr('href');
        var index = href.indexOf(library) + 1;
        var currentTheme = href.substring(index + library.length);

        this.replaceLink(linkElement, href.replace(currentTheme, theme));
        this.changeLayout(theme);
    },

    changeTopbarColor: function(topbarColorClass) {
        var wrapper = $('.layout-wrapper'),
        classes = wrapper.attr('class'),
        startIndexOf = classes.indexOf('layout-topbar-'),
        endIndexOf = classes.indexOf(' ', startIndexOf);
        endIndexOf = endIndexOf === -1 ? classes.length : endIndexOf;

        var currentTopbarColor = classes.substring(startIndexOf, endIndexOf);

        wrapper.removeClass(currentTopbarColor).addClass(topbarColorClass);
    },

    changeSectionTheme: function(theme, section) {
        var wrapperElement = $('.layout-wrapper');
     
        var styleClass = wrapperElement.attr('class');
        var tokens = styleClass.split(' ');
        var sectionClass;
        for (var i = 0; i < tokens.length; i++) {
            if (tokens[i].indexOf(section + '-') > -1) {
                sectionClass = tokens[i];
                break;
            }
        }

        wrapperElement.attr('class', styleClass.replace(sectionClass, section + '-' + theme));
    },

    beforeResourceChange: function() {
        PrimeFaces.ajax.RESOURCE = null;    //prevent resource append
    },
    
    replaceLink: function(linkElement, href) {
        PrimeFaces.ajax.RESOURCE = 'javax.faces.Resource';

        var isIE = this.isIE();

        if (isIE) {
            linkElement.attr('href', href);
        }
        else {
            var cloneLinkElement = linkElement.clone(false);

            cloneLinkElement.attr('href', href);
            linkElement.after(cloneLinkElement);
            
            cloneLinkElement.off('load').on('load', function() {
                linkElement.remove();
            });
        }
    },


    changeMenuMode: function(menuMode) {
        var wrapper = $(document.body).children('.layout-wrapper');
        switch (menuMode) {
            case 'layout-static layout-static-active':
                wrapper.addClass('layout-static layout-static-active').removeClass('layout-overlay layout-slim layout-horizontal ');
                this.clearLayoutState();
            break;

            case 'layout-overlay':
                wrapper.addClass('layout-overlay').removeClass('layout-static layout-slim layout-horizontal layout-static-active');
                this.clearLayoutState();
            break;

            case 'layout-horizontal':
                wrapper.addClass('layout-horizontal').removeClass('layout-static layout-overlay  layout-slim  layout-static-active');
                this.clearLayoutState();
            break;

            case 'layout-slim':
                wrapper.addClass('layout-slim').removeClass('layout-static layout-overlay layout-horizontal layout-static-active');
                this.clearLayoutState();
            break;

            default:
                wrapper.addClass('layout-static').removeClass('layout-overlay layout-slim layout-horizontal ');
                this.clearLayoutState();
            break;
        }
    },

    changeMenuToRTL: function() {
        $('.layout-wrapper').toggleClass('layout-rtl');
        this.updateNano($('.layout-wrapper').hasClass('layout-rtl'));
    },
    
    updateNano: function(isRTL) {
        var nanoPanel = $('.layout-wrapper').find('.nano');
        nanoPanel.nanoScroller({destroy: true});
        nanoPanel.nanoScroller({isRTL: isRTL});
    },

    clearLayoutState: function() {
        var menu = PF('RomaMenuWidget');

        if (menu) {
            menu.clearLayoutState();
        }
    },
    
    isIE: function() {
        return /(MSIE|Trident\/|Edge\/)/i.test(navigator.userAgent);
    },

    updateInputStyle: function(value) {
        if (value === 'filled')
            $(document.body).addClass('ui-input-filled');
        else
            $(document.body).removeClass('ui-input-filled');
    }
};

/*!
 * jQuery Cookie Plugin v1.4.1
 * https://github.com/carhartl/jquery-cookie
 *
 * Copyright 2006, 2014 Klaus Hartl
 * Released under the MIT license
 */
(function (factory) {
	if (typeof define === 'function' && define.amd) {
		// AMD (Register as an anonymous module)
		define(['jquery'], factory);
	} else if (typeof exports === 'object') {
		// Node/CommonJS
		module.exports = factory(require('jquery'));
	} else {
		// Browser globals
		factory(jQuery);
	}
}(function ($) {

	var pluses = /\+/g;

	function encode(s) {
		return config.raw ? s : encodeURIComponent(s);
	}

	function decode(s) {
		return config.raw ? s : decodeURIComponent(s);
	}

	function stringifyCookieValue(value) {
		return encode(config.json ? JSON.stringify(value) : String(value));
	}

	function parseCookieValue(s) {
		if (s.indexOf('"') === 0) {
			// This is a quoted cookie as according to RFC2068, unescape...
			s = s.slice(1, -1).replace(/\\"/g, '"').replace(/\\\\/g, '\\');
		}

		try {
			// Replace server-side written pluses with spaces.
			// If we can't decode the cookie, ignore it, it's unusable.
			// If we can't parse the cookie, ignore it, it's unusable.
			s = decodeURIComponent(s.replace(pluses, ' '));
			return config.json ? JSON.parse(s) : s;
		} catch(e) {}
	}

	function read(s, converter) {
		var value = config.raw ? s : parseCookieValue(s);
		return $.isFunction(converter) ? converter(value) : value;
	}

	var config = $.cookie = function (key, value, options) {

		// Write

		if (arguments.length > 1 && !$.isFunction(value)) {
			options = $.extend({}, config.defaults, options);

			if (typeof options.expires === 'number') {
				var days = options.expires, t = options.expires = new Date();
				t.setMilliseconds(t.getMilliseconds() + days * 864e+5);
			}

			return (document.cookie = [
				encode(key), '=', stringifyCookieValue(value),
				options.expires ? '; expires=' + options.expires.toUTCString() : '', // use expires attribute, max-age is not supported by IE
				options.path    ? '; path=' + options.path : '',
				options.domain  ? '; domain=' + options.domain : '',
				options.secure  ? '; secure' : ''
			].join(''));
		}

		// Read

		var result = key ? undefined : {},
			// To prevent the for loop in the first place assign an empty array
			// in case there are no cookies at all. Also prevents odd result when
			// calling $.cookie().
			cookies = document.cookie ? document.cookie.split('; ') : [],
			i = 0,
			l = cookies.length;

		for (; i < l; i++) {
			var parts = cookies[i].split('='),
				name = decode(parts.shift()),
				cookie = parts.join('=');

			if (key === name) {
				// If second argument (value) is a function it's a converter...
				result = read(cookie, value);
				break;
			}

			// Prevent storing a cookie that we couldn't decode.
			if (!key && (cookie = read(cookie)) !== undefined) {
				result[name] = cookie;
			}
		}

		return result;
	};

	config.defaults = {};

	$.removeCookie = function (key, options) {
		// Must not alter options, thus extending a fresh object...
		$.cookie(key, '', $.extend({}, options, { expires: -1 }));
		return !$.cookie(key);
	};

}));

if (PrimeFaces.widget.InputSwitch) {
    PrimeFaces.widget.InputSwitch = PrimeFaces.widget.InputSwitch.extend({

        init: function (cfg) {
            this._super(cfg);

            if (this.input.prop('checked')) {
                this.jq.addClass('ui-inputswitch-checked');
            }
        },

        check: function () {
            var $this = this;
           
            this.input.prop('checked', true).trigger('change');
            setTimeout(function(){ 
                $this.jq.addClass('ui-inputswitch-checked');
            },100);
        },

        uncheck: function () {
            var $this = this;
            
            this.input.prop('checked', false).trigger('change');
            setTimeout(function(){ 
                $this.jq.removeClass('ui-inputswitch-checked');
            }, 100);
        }
    });
}

PrimeFaces.widget.SelectManyCheckbox.prototype.bindEvents = function() {
    this.outputs.filter(':not(.ui-state-disabled)').on('mouseover', function() {
        $(this).addClass('ui-state-hover');
    })
    .on('mouseout', function() {
        $(this).removeClass('ui-state-hover');
    })
    .on('click', function() {
        var checkbox = $(this),
        input = checkbox.prev().children(':checkbox');

        input.trigger('click');
        input.trigger('focus');

        if($.browser.msie && parseInt($.browser.version) < 9) {
            input.trigger('change');
        }
    });

    //delegate focus-blur-change states
    this.enabledInputs.on('focus', function() {
        var input = $(this),
        checkbox = input.parent().next();

        checkbox.addClass('ui-state-focus');
    })
    .on('blur', function() {
        var input = $(this),
        checkbox = input.parent().next();

        checkbox.removeClass('ui-state-focus');
    })
    .on('change', function(e) {
        var input = $(this),
        checkbox = input.parent().next(),
        disabled = input.is(':disabled');

        if(disabled) {
            return;
        }

        if(input.is(':checked')) {
            checkbox.children('.ui-chkbox-icon').removeClass('ui-icon-blank').addClass('ui-icon-check');

            checkbox.addClass('ui-state-active');
        }
        else {
            checkbox.removeClass('ui-state-active').children('.ui-chkbox-icon').addClass('ui-icon-blank').removeClass('ui-icon-check');
        }
    });
}

PrimeFaces.widget.SelectOneRadio = PrimeFaces.widget.BaseWidget.extend({

    init: function(cfg) {
        this._super(cfg);

        //custom layout
        if(this.cfg.custom) {
            this.originalInputs = this.jq.find(':radio');
            this.inputs = $('input:radio[name="' + this.id + '"].ui-radio-clone');
            this.outputs = this.inputs.parent().next('.ui-radiobutton-box');
            this.labels = $();

            //labels
            for(var i=0; i < this.outputs.length; i++) {
                this.labels = this.labels.add('label[for="' + this.outputs.eq(i).parent().attr('id') + '"]');
            }

            //update radio state
            for(var i = 0; i < this.inputs.length; i++) {
                var input = this.inputs.eq(i),
                itemindex = input.data('itemindex'),
                original = this.originalInputs.eq(itemindex);

                input.val(original.val());

                if(original.is(':checked')) {
                    input.prop('checked', true).parent().next().addClass('ui-state-active').children('.ui-radiobutton-icon')
                            .addClass('ui-icon-bullet').removeClass('ui-icon-blank');
                }
            }
            
            //pfs metadata
            this.originalInputs.data(PrimeFaces.CLIENT_ID_DATA, this.id);
        }
        //regular layout
        else {
            this.outputs = this.jq.find('.ui-radiobutton-box');
            this.inputs = this.jq.find(':radio');
            this.labels = this.jq.find('label');
            
            //pfs metadata
            this.inputs.data(PrimeFaces.CLIENT_ID_DATA, this.id);
        }

        this.enabledInputs = this.inputs.filter(':not(:disabled)');
        this.checkedRadio = this.outputs.filter('.ui-state-active');

        this.bindEvents();
    },

    bindEvents: function() {
        var $this = this;

        this.outputs.filter(':not(.ui-state-disabled)').on('mouseover.selectOneRadio', function() {
            $(this).addClass('ui-state-hover');
        })
        .on('mouseout.selectOneRadio', function() {
            $(this).removeClass('ui-state-hover');
        })
        .on('click.selectOneRadio', function(e) {
            var radio = $(this),
            input = radio.prev().children(':radio');

            if(!radio.hasClass('ui-state-active')) {
                $this.unselect($this.checkedRadio);
                $this.select(radio);
                $this.fireClickEvent(input, e);
                input.trigger('change');
            }
            else {
                $this.fireClickEvent(input, e);
            }

            input.trigger('focus');
        });

        this.labels.filter(':not(.ui-state-disabled)').on('click.selectOneRadio', function(e) {
            var target = $(PrimeFaces.escapeClientId($(this).attr('for'))),
            radio = null;

            //checks if target is input or not(custom labels)
            if(target.is(':input'))
                radio = target.parent().next();
            else
                radio = target.children('.ui-radiobutton-box'); //custom layout

            radio.trigger('click.selectOneRadio');

            e.preventDefault();
        });

        this.enabledInputs.on('focus.selectOneRadio', function() {
            var input = $(this),
            radio = input.parent().next();

            radio.addClass('ui-state-focus');
        })
        .on('blur.selectOneRadio', function() {
            var input = $(this),
            radio = input.parent().next();

            radio.removeClass('ui-state-focus');
        })
        .on('keydown.selectOneRadio', function(e) {
            var input = $(this),
            currentRadio = input.parent().next(),
            index = $this.enabledInputs.index(input),
            size = $this.enabledInputs.length,
            keyCode = $.ui.keyCode,
            key = e.which;

            switch(key) {
                case keyCode.UP:
                case keyCode.LEFT:
                    var prevRadioInput = (index === 0) ? $this.enabledInputs.eq((size - 1)) : $this.enabledInputs.eq(--index),
                    prevRadio = prevRadioInput.parent().next();

                    input.blur();
                    $this.unselect(currentRadio);
                    $this.select(prevRadio);
                    prevRadioInput.trigger('focus').trigger('change');
                    e.preventDefault();
                break;

                case keyCode.DOWN:
                case keyCode.RIGHT:
                    var nextRadioInput = (index === (size - 1)) ? $this.enabledInputs.eq(0) : $this.enabledInputs.eq(++index),
                    nextRadio = nextRadioInput.parent().next();

                    input.blur();
                    $this.unselect(currentRadio);
                    $this.select(nextRadio);
                    nextRadioInput.trigger('focus').trigger('change');
                    e.preventDefault();
                break;

                case keyCode.SPACE:
                    if(!input.prop('checked')) {
                        $this.select(currentRadio);
                        input.trigger('focus').trigger('change');
                    }

                    e.preventDefault();
                break;
            }
        });
    },

    unselect: function(radio) {
        var radioInput = radio.prev().children(':radio');
        radioInput.prop('checked', false);
        radio.removeClass('ui-state-active').children('.ui-radiobutton-icon').removeClass('ui-icon-bullet').addClass('ui-icon-blank');
        
        if (this.cfg.custom) {
            var itemindex = radioInput.data('itemindex');
            this.originalInputs.eq(itemindex).prop('checked', false);
        }
    },

    select: function(radio) {
        var radioInput = radio.prev().children(':radio');
        this.checkedRadio = radio;
        radio.addClass('ui-state-active').children('.ui-radiobutton-icon').addClass('ui-icon-bullet').removeClass('ui-icon-blank');
        radioInput.prop('checked', true);
        
        if (this.cfg.custom) {
            var itemindex = radioInput.data('itemindex');
            this.originalInputs.eq(itemindex).prop('checked', true);
        }
    },

    unbindEvents: function(input) {
        if(input) {
            input.off();
            input.parent().nextAll('.ui-radiobutton-box').off();
            this.labels.filter("label[for='" + input.attr('id') + "']").off();
        }
        else {
            this.inputs.off();
            this.labels.off();
            this.outputs.off();
        }
    },

    disable: function(index) {
        if(index == null) {
            this.inputs.attr('disabled', 'disabled');
            this.labels.addClass('ui-state-disabled');
            this.outputs.addClass('ui-state-disabled');
            this.unbindEvents();
        }
        else {
            var input = this.inputs.eq(index),
                label = this.labels.filter("label[for='" + input.attr('id') + "']");
            input.attr('disabled', 'disabled').parent().nextAll('.ui-radiobutton-box').addClass('ui-state-disabled');
            label.addClass('ui-state-disabled');
            this.unbindEvents(input);
        }

    },

    enable: function(index) {
        if(index == null) {
            this.inputs.removeAttr('disabled');
            this.labels.removeClass('ui-state-disabled');
            this.outputs.removeClass('ui-state-disabled');
        }
        else {
            var input = this.inputs.eq(index),
                label = this.labels.filter("label[for='" + input.attr('id') + "']");
            input.removeAttr('disabled').parent().nextAll('.ui-radiobutton-box').removeClass('ui-state-disabled');
            label.removeClass('ui-state-disabled');
        }
        this.bindEvents();
    },
    
    fireClickEvent: function(input, event) {
        var userOnClick = input.prop('onclick');
        if (userOnClick) {
            userOnClick.call(this, event);
        }
    }

});


/**
 * PrimeFaces SelectBooleanCheckbox Widget
 */
PrimeFaces.widget.SelectBooleanCheckbox = PrimeFaces.widget.BaseWidget.extend({

    init: function(cfg) {
        this._super(cfg);

        this.input = $(this.jqId + '_input');
        this.box = this.jq.find('.ui-chkbox-box');
        this.icon = this.box.children('.ui-chkbox-icon');
        this.itemLabel = this.jq.find('.ui-chkbox-label');
        this.disabled = this.input.is(':disabled');

        var $this = this;

        //bind events if not disabled
        if(!this.disabled) {
            this.box.on('mouseover.selectBooleanCheckbox', function() {
                $this.box.addClass('ui-state-hover');
            })
            .on('mouseout.selectBooleanCheckbox', function() {
                $this.box.removeClass('ui-state-hover');
            })
            .on('click.selectBooleanCheckbox', function() {
                $this.input.trigger('click');
                $this.input.trigger('focus');
            });

            this.input.on('focus.selectBooleanCheckbox', function() {
                $this.box.addClass('ui-state-focus');
            })
            .on('blur.selectBooleanCheckbox', function() {
                $this.box.removeClass('ui-state-focus');
            })
            .on('change.selectBooleanCheckbox', function(e) {
                if($this.isChecked())
                    $this.box.addClass('ui-state-active').children('.ui-chkbox-icon').removeClass('ui-icon-blank').addClass('ui-icon-check');
                else
                    $this.box.removeClass('ui-state-active').children('.ui-chkbox-icon').addClass('ui-icon-blank').removeClass('ui-icon-check');
            });

            //toggle state on label click
            this.itemLabel.click(function() {
                $this.toggle();
                $this.input.trigger('focus');
            });
        }

        //pfs metadata
        this.input.data(PrimeFaces.CLIENT_ID_DATA, this.id);
    },

    toggle: function() {
        if(this.isChecked())
            this.uncheck();
        else
            this.check();
    },

    isChecked: function() {
        return this.input.prop('checked');
    },

    check: function() {
        if(!this.isChecked()) {
            this.input.prop('checked', true).trigger('change');
            this.input.attr('aria-checked', true);
            this.box.addClass('ui-state-active').children('.ui-chkbox-icon').removeClass('ui-icon-blank').addClass('ui-icon-check');
        }
    },

    uncheck: function() {
        if(this.isChecked()) {
            this.input.prop('checked', false).trigger('change');
            this.input.attr('aria-checked', false);
            this.box.removeClass('ui-state-active').children('.ui-chkbox-icon').addClass('ui-icon-blank').removeClass('ui-icon-check');
        }
    }

});