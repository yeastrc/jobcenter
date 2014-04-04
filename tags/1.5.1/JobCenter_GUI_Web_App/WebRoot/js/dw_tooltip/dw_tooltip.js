/*************************************************************************
    This code is from Dynamic Web Coding at dyn-web.com
    Copyright 2003-2008 by Sharon Paine 
    See Terms of Use at www.dyn-web.com/business/terms.php
    regarding conditions under which you may use this code.
    This notice must be retained in the code as is!
    
    version date: Aug 2008
    requires: dw_event.js (april 2008 version) 
        and dw_viewport.js (march 2008 version)
*************************************************************************/

var dw_Tooltip = {
    offX: 12,
    offY: 12,
    showDelay: 100,
    hideDelay: 100,
    hoverDelay: 500, // for hover tip
    tipID: "tipDiv",
    actuatorClass: "showTip",
    maxLoops: 2, // for actuator check (linked image, etc.)
    activateOnfocus: true,
    tip: null, shim:null, timer: 0, hoverTimer: 0,
    active: false, actuator: null, resetFlag: false, restored: true,
    on_show: function() {}, on_position: function() {}, on_hide: function() {},
    
    init: function() {
        var _this = dw_Tooltip;
        if ( document.createElement && document.body && typeof document.body.appendChild != "undefined" ) {
            var el = document.createElement("div");
            el.id = _this.tipID; el.style.position = 'absolute';
            el.style.visibility = 'hidden'; el.style.zIndex = 10000;
            document.body.appendChild(el);
            _this.tip = document.getElementById( _this.tipID);
            _this.setDefaults();
            if ( _this.checkOverlaySupport() ) { _this.prepOverlay(); }
            _this.setPosition(0, 0);
        }
    },
    
    setDefaults: function() { // called when props changed (resetFlag set)
        if ( !this.defaultProps ) this.defaultProps = {};
        // prop name, type, default
        var list = [  ['followMouse', 'boolean', true], ['sticky', 'boolean', false], ['klass', 'string', ''],
            ['hoverable', 'boolean', false], ['duration', 'number', 0], 
            ['jumpAbove', 'boolean', true], ['jumpLeft', 'boolean', true],
            ['Left', 'boolean', false], ['Above', 'boolean', false],
            ['positionFn', 'function', this.positionRelEvent], 
            ['wrapFn', 'function', function(str) { return str; } ]  ];
        
        for (var i=0; list[i]; i++) {
            this[ list[i][0] ] = ( typeof this.defaultProps[ list[i][0] ] == list[i][1] )? 
                this.defaultProps[ list[i][0] ]: list[i][2];
        }
        
        this.tip.className = this.klass;
        this.coordinateOptions();
    },
    
    activate: function(e, tgt, msg, id) {
        var _this = dw_Tooltip; if (!_this.tip) return;
        _this.clearTimer('timer');  _this.clearTimer('hoverTimer');
        if ( !_this.restored ) _this.handleRestore();
        _this.actuator = tgt; dw_Viewport.getAll();  
        _this.getContent(e, tgt, msg, id); _this.restored = false;
        if ( !_this.tip.innerHTML ) return; _this.active = true;
        _this.handleOptions(e);  _this.positionFn(e, tgt); _this.adjust();
        _this.timer = setTimeout(_this.show, _this.showDelay);
    },

    getContent: function(e, tgt, msg, id) {
        msg = msg || '';
        if (id && !msg) {
            var obj = (id && this.content_vars && this.content_vars[id])? this.content_vars[id]: false;
            if ( typeof obj == 'string' ) {
                msg = obj;
            } else if ( typeof obj == 'object' ) {
                this.checkForProps( obj );
                if ( obj['content'] ) { 
                    msg = obj['content'];
                } else if ( obj['html_id'] ) { // id of page element
                    var el = document.getElementById( obj['html_id'] ); 
                    if (el) msg = el.innerHTML;
                } else { 
                    msg = obj;  // wrapFn will obtain props from obj 
                }
            }
        }        
        this.writeTip(msg);
    },
    
    writeTip: function(msg, bReqFlag) {
        if ( this.pendingReq && this.respRecd && !bReqFlag ) return;
        msg = this.wrapFn(msg); this.tip.innerHTML = msg;
    },
    
    positionRelEvent: function(e, tgt) {
        var _this = dw_Tooltip; 
        if (typeof e == 'object') { // event 
            if ( e.type == 'mouseover' || e.type == 'mousemove' ) {
                _this.evX = _this.getMouseEventX(e);
                _this.evY = _this.getMouseEventY(e);
            } else { // focus
                var pos = dw_getPageOffsets( tgt );
                _this.evX = pos.x;
                _this.evY = pos.y;
            }
        }
        
        var coords = _this.calcPosCoords(e, tgt);
        _this.setPosition(coords.x, coords.y);
    },
    
    calcPosCoords: function(e, tgt) {
        var x = this.evX; var y = this.evY; var xXd, yXd;
        var maxX = this.getMaxX(); var maxY = this.getMaxY(); // tip width/height too
        
        var tx = x + this.offX;
        var altx = x - ( this.width + this.offX );
        var spL =  x - dw_Viewport.scrollX > dw_Viewport.width/2;
        
        if ( typeof e == 'object' && e.type && ( e.type == 'focus' || e.type == 'focusin' ) ) {
            var tgtWidth = tgt.offsetWidth;
            if ( tx + tgtWidth  < maxX ) {
                x = this.evX = x + tgtWidth;
                tx += tgtWidth; 
            } else if (tx + 20 < maxX ) {
                x = this.evX = x + 20;
                tx += 20
            }
            y = this.evY = y + 10;
        }
        
        var ty = y + this.offY;
        var alty = y - ( this.height + this.offY );
        var spA =  y - dw_Viewport.scrollY > dw_Viewport.height/2;
        
        if ( !this.Left && tx < maxX ) {
            x = tx;
        } else if ( ( this.Left && altx >= dw_Viewport.scrollX ) || 
                ( this.jumpLeft && tx >= maxX && altx >= dw_Viewport.scrollX ) ) {
            x = altx;
        } else if ( ( this.Left && altx < dw_Viewport.scrollX ) || 
                ( !this.Left && this.jumpLeft && altx < dw_Viewport.scrollX && spL ) ) {
            x = dw_Viewport.scrollX; // place at left edge
            xXd = 'Left'; // check later whether yXd too
        } else if ( !this.Left && tx >= maxX && ( !this.jumpLeft || 
                ( this.jumpLeft && altx < dw_Viewport.scrollX && !spL ) ) ) {
            x = maxX; xXd = 'Right';
        }
        
        if ( !this.Above && ty < maxY ) {
            y = ty;
        } else if ( ( this.Above && alty >= dw_Viewport.scrollY ) || 
                ( this.jumpAbove && ty >= maxY && alty >= dw_Viewport.scrollY ) ) {
            y = alty;
        } else if ( ( this.Above && alty < dw_Viewport.scrollY ) || 
                ( !this.Above && this.jumpAbove && alty < dw_Viewport.scrollY && spA )  ) {
            y = dw_Viewport.scrollY; // place at top
            yXd = 'Above';
        } else if ( !this.Above && ty >= maxY && ( !this.jumpAbove || 
                ( this.jumpAbove && alty < dw_Viewport.scrollY && !spA ) ) ) {
            y = maxY; yXd = 'Below';
        }
        
        if ( xXd && yXd ) { // over link (will flicker) calc least distance to uncover
            var dx = (xXd == 'Left')? dw_Viewport.scrollX - altx: tx - maxX;
            var dy = (yXd == 'Above')? dw_Viewport.scrollY - alty: ty - maxY;
            if ( dx <= dy ) {
                x = (xXd == 'Left')? altx: tx;
            } else {
                y = (yXd == 'Above')? alty: ty;
            }
        }
        return { x: x, y: y }
    },
    
    adjust: function() {
        var _this = dw_Tooltip;
        var imgs = _this.tip.getElementsByTagName('img');
        var img = imgs.length? imgs[imgs.length - 1]: null;
        checkComplete();
        
        function checkComplete() {
            if ( !_this.active ) return;
             _this.positionFn();
            if (img && !img.complete) {
                setTimeout( checkComplete, 50);
            }
        }
    },
    
    setPosition: function(x, y) {
        this.tip.style.left = x + 'px';
        this.tip.style.top = y + 'px';
        this.setOverlay(); this.on_position();
    },

    show: function() {
        var _this = dw_Tooltip;
        _this.tip.style.visibility = 'visible';
        if ( _this.shim ) _this.shim.style.visibility = 'visible';
        _this.on_show();
    },

    deactivate: function(e) {
        var _this = dw_Tooltip; if (!_this.tip || !_this.active || _this.sticky ) return;
        e = e? e: window.event;
        if (e.type && e.type == 'mouseout' && !dw_mouseleave(e, _this.actuator) ) return;
        _this.clearTimer('timer');  _this.clearTimer('hoverTimer');
        
        if ( _this.hoverable ) { // delayed call to hide (time to check if hovered over tip)
            _this.hoverTimer = setTimeout( _this.hide, _this.hoverDelay );
            return;
        }
        if ( _this.duration ) {
            _this.timer = setTimeout( _this.hide, _this.duration );
            return;
        }
        _this.timer = setTimeout( _this.hide, _this.hideDelay );
    },
    
    hide: function() {
        var _this = dw_Tooltip; if (!_this.tip) return;
        _this.tip.style.visibility = 'hidden';
        if ( _this.shim ) _this.shim.style.visibility = 'hidden';
        _this.handleRestore(); _this.on_hide();
    },
    
    handleOptions: function(e) {
        this.coordinateOptions();
        if ( this.klass ) { this.tip.className = this.klass; }
        if ( this.hoverable ) {
            this.tip.onmouseout = dw_Tooltip.tipOutCheck;
            this.tip.onmouseover = function() { dw_Tooltip.clearTimer('hoverTimer'); }
        }
        if ( this.followMouse && !this.hoverable && !(e.type == 'focus' || e.type == 'focusin') ) {
            dw_Event.add(document, 'mousemove', this.positionRelEvent);
        }
        
        if ( this.sticky || this.duration ) {
            dw_Event.add( document, "mouseup", dw_Tooltip.checkDocClick );
        }
    },
    
    coordinateOptions: function() {
        if ( this.sticky || this.hoverable || this.duration ) { this.followMouse = false; }
        if ( this.sticky ) { this.hoverable = false; this.duration = 0; }
        if ( this.hoverable ) { this.duration = 0; }
        if ( this.positionFn != this.positionRelEvent ) this.followMouse = false;
    },

    handleRestore: function() {
        if ( this.followMouse ) {
            dw_Event.remove(document, 'mousemove', this.positionRelEvent);
        }
        if ( this.sticky || this.duration ) {
            dw_Event.remove( document, "mouseup",   dw_Tooltip.checkDocClick, false );
        }
        this.tip.onmouseover = this.tip.onmouseout = function() {}
        
        if ( this.resetFlag ) this.setDefaults(); 
        this.writeTip('');
        
        this.active = false; this.actuator = null;
        this.tip.style.width = ''; 
        this.restored = true;
    },
    
    // first class name is actuatorClass, second class would point to content 
    getTipClass: function(cls) {
        if (!cls) return ''; var c = '';
        var classes = cls.split(/\s+/);
        if ( classes[0] == this.actuatorClass && classes[1] ) {
            c = classes[1];
        }
        return c; // return second class name or ''
    },
    
    checkForProps: function(obj) {
        var list = ['jumpAbove', 'jumpLeft', 'Above', 'Left', 'sticky', 'duration', 
                'hoverable', 'followMouse', 'klass', 'positionFn', 'wrapFn'];
        for (var i=0; list[i]; i++) {
            if ( typeof obj[ list[i] ] != 'undefined' ) {
                this[ list[i] ] = obj[ list[i] ];
                this.resetFlag = true;
            }
        }
    },

    tipOutCheck: function(e) { // hover tip
        var _this = dw_Tooltip; e = dw_Event.DOMit(e);
        var tip = this; // assigned to onmouseover property of tip
        if ( dw_mouseleave(e, tip) ) {
            _this.timer = setTimeout( _this.hide, _this.hideDelay);
        }
    },

    checkEscKey: function(e) { // for sticky, duration, and onfocus activation
        e = e? e: window.event;  if ( e.keyCode == 27 ) dw_Tooltip.hide();
    },

    checkDocClick: function(e) { 
        if ( !dw_Tooltip.active ) return;
        var tgt = dw_Event.getTarget(e);
        // hide tooltip if you click anywhere in the document 
        // except on the tooltip, unless that click is on the tooltip's close box    
        var tip = document.getElementById(dw_Tooltip.tipID);
        if ( tgt == tip || dw_contained(tgt, tip) ) {
            if ( tgt.tagName && tgt.tagName.toLowerCase() == "img" ) tgt = tgt.parentNode; 
            if ( tgt.tagName.toLowerCase() != "a" || tgt.href.indexOf("dw_Tooltip.hide") != -1 ) return;
        }
        // slight delay to avoid crossing onfocus activation and doc click hide 
        dw_Tooltip.timer = setTimeout( dw_Tooltip.hide, 50);
    },
    
    // check need for and support of iframe shim (for ie win and select lists)
    checkOverlaySupport: function() {
        if ( navigator.userAgent.indexOf("Windows") != -1 && 
            typeof document.body != "undefined" && 
            typeof document.body.insertAdjacentHTML != "undefined" && 
            !window.opera && navigator.appVersion.indexOf("MSIE 5.0") == -1 
            ) return true;
        return false;
    }, 
    
    prepOverlay: function() {
        document.body.insertAdjacentHTML("beforeEnd", '<iframe id="tipShim" src="javascript: false" style="position:absolute; left:0; top:0; z-index:500; visibility:hidden" scrolling="no" frameborder="0"></iframe>');
        this.shim = document.getElementById('tipShim'); 
        if (this.shim && this.tip) {
            this.shim.style.width = this.tip.offsetWidth + "px";
            this.shim.style.height = this.tip.offsetHeight + "px";
        }
    },
    
    setOverlay: function() { // position and dimensions
        if ( this.shim ) {
            this.shim.style.left = this.tip.style.left;
            this.shim.style.top = this.tip.style.top;
            this.shim.style.width = this.tip.offsetWidth + "px";
            this.shim.style.height = this.tip.offsetHeight + "px";
        }
    },
    
    clearTimer: function(timer) {
        if ( dw_Tooltip[timer] ) { clearTimeout( dw_Tooltip[timer] ); dw_Tooltip[timer] = 0; }
    },
    
    getWidth: function() { return this.width = this.tip.offsetWidth; },
    getHeight: function() { return this.height = this.tip.offsetHeight; },
    getMaxX: function() { return dw_Viewport.width + dw_Viewport.scrollX - this.getWidth() - 1; },
    getMaxY: function() { return dw_Viewport.height + dw_Viewport.scrollY - this.getHeight() - 1; },
    getMouseEventX: function(e) { return e.pageX? e.pageX: e.clientX + dw_Viewport.scrollX; },
    getMouseEventY: function(e) { return e.pageY? e.pageY: e.clientY + dw_Viewport.scrollY; }
    
}

// code for event delegation
dw_Tooltip.initHandlers = function () {
    dw_Event.add(document, 'mouseover', dw_Tooltip.checkActuatorMouseover);
    dw_Event.add( document, "keydown", dw_Tooltip.checkEscKey,  true ); // for sticky 
    dw_Event.add( window, 'blur', dw_Tooltip.deactivate, true ); 
    dw_Event.add( window, 'unload', dw_Tooltip.deactivate, true ); // firefox needs
    
    // see http://www.quirksmode.org/blog/archives/2008/04/delegating_the.html
    if ( dw_Tooltip.activateOnfocus ) {
        document.onfocusin = dw_Tooltip.checkActuatorFocus; // ie
        if ( window.addEventListener ) {
            dw_Event.add(document, 'focus', dw_Tooltip.checkActuatorFocus, true);
        }
    }
}

dw_Tooltip.checkActuatorMouseover = function (e) {
    var tgt = dw_Event.getTarget(e); var tipAct = null;
    // limit number of loops 
    var ctr = 0; var maxCnt = dw_Tooltip.maxLoops; 
    do {
        if ( tipAct = dw_Tooltip.getActuatorInfo(tgt) ) {
            var msg = tipAct.msg; var id = tipAct.id;
            dw_Tooltip.activate(e, tgt, msg, id);
            if ( window.attachEvent ) { // avoid multiples for ie (?)
                dw_Event.remove( tgt, 'mouseout', dw_Tooltip.deactivate); 
            }
            dw_Event.add( tgt, 'mouseout', dw_Tooltip.deactivate); 
            break;
        }
        ctr++;
    } while ( ctr < maxCnt && (tgt = tgt.parentNode) ); 

}

dw_Tooltip.checkActuatorFocus = function (e) {
    e = e? e: window.event; var tipAct = null;
    var tgt = dw_Event.getTarget(e);
    if ( tgt && (tipAct = dw_Tooltip.getActuatorInfo(tgt) ) ) {
        if ( dw_Tooltip.active && tgt == dw_Tooltip.actuator ) { 
            return; // if already activated onmouseover
        }
        var msg = tipAct.msg; var id = tipAct.id;
        dw_Tooltip.activate(e, tgt, msg, id);
        if ( window.attachEvent ) { 
            tgt.onfocusout = dw_Tooltip.deactivate;
        } else {
            dw_Event.add( tgt, 'blur', dw_Tooltip.deactivate, true); 
        }
    }
}

// Check whether the target is an actuator and the content can be located 
// Either the content itself or the identifier in content_vars will be returned in obj {msg: msg, id: id}
dw_Tooltip.getActuatorInfo = function (tgt) {
    var qual = dw_Tooltip.defaultProps['actuatorQual'] || 'actuatorClass';
    var source = dw_Tooltip.defaultProps['content_source'] || 'content_vars';
    var msg = '', id = '';
    dw_Tooltip.resetReqFlags();
    switch (qual) {
        case 'actuatorClass' : 
            var cls = dw_Tooltip.getTipClass(tgt.className);
            if (!cls) break;
            if ( source == 'content_vars' ) {
                id = (dw_Tooltip.content_vars && dw_Tooltip.content_vars[cls])? cls: '';
            } else if ( source == 'class_id' ) {
                var el = document.getElementById(cls);
                if (el) msg = el.innerHTML;
            }
            break;
        case 'queryVal' :
            var queryVal = dw_Tooltip.defaultProps['queryVal'];
            var val = queryVal? dw_getValueFromQueryString(queryVal, tgt ): '';
            id = (val && dw_Tooltip.content_vars && dw_Tooltip.content_vars[val])? val: '';
            // Even if the content source is ajax, would check content_vars (see below)
            // dw_updateTooltip should be set up to save results in content_vars
            if ( val && source == 'ajax' && !id ) {
                // Something to display in the tooltip while awaiting response. Empty string won't suffice!  
                msg = 'Retrieving info ...'; 
                dw_Tooltip.pendingReq = true; 
                var queryData = encodeURIComponent(queryVal) + '=' + encodeURIComponent(val);
                dw_TooltipRequest( queryData, val ); // val passed as means to save response
            }
            break;
        case 'id' :
            id = (tgt.id && dw_Tooltip.content_vars && dw_Tooltip.content_vars[tgt.id])? tgt.id: '';
            break;
    }
    //if ( id && !msg ) { // check content_vars (for previously saved ajax result, or more complex data for ajax request)  }
    if ( id || msg ) {
        return {msg: msg, id: id}
    }
    return false;
}

// check so don't overwrite response if already received (localhost speed)
dw_Tooltip.resetReqFlags = function () {
    this.respRecd = false;
    this.pendingReq = false;
}

/////////////////////////////////////////////////////////////////////
// Helper functions 
function dw_mouseleave(e, oNode) {
    e = dw_Event.DOMit(e);
    var toEl = e.relatedTarget? e.relatedTarget: e.toElement? e.toElement: null;
    if ( oNode != toEl && !dw_contained(toEl, oNode) ) {
        return true;
    }
    return false;
}

function dw_contained(oNode, oCont) {
    if (!oNode) return; // in case alt-tab away while hovering (prevent error)
    while ( oNode = oNode.parentNode ) if ( oNode == oCont ) return true;
    return false;
}

// Get position of element in page (treacherous cross-browser territory! Don't expect perfect results)
// can get weird results in ie
function dw_getPageOffsets(el) {
	var left = 0, top = 0;
    do {
        left += el.offsetLeft;
        top += el.offsetTop;
    } while (el = el.offsetParent);
    return { x:left, y:top };
}

// obj: link or window.location
function dw_getValueFromQueryString(name, obj) {
    obj = obj? obj: window.location; 
    if (obj.search && obj.search.indexOf(name != -1) ) {
        var pairs = obj.search.slice(1).split("&"); // name/value pairs
        var set;
        for (var i=0; pairs[i]; i++) {
            set = pairs[i].split("="); // Check each pair for match on name 
            if ( set[0] == name && set[1] ) {
                return set[1];
            }
        }
    }
    return '';
}

/////////////////////////////////////////////////////////////////////
// Reminder about licensing requirements
// See Terms of Use at www.dyn-web.com/business/terms.php
// OK to remove after purchasing a license or if using on a personal site.
function dw_checkAuth() {
    var loc = window.location.hostname.toLowerCase();
    var msg = 'A license is required for commercial use of this code.\n' + 
        'Please adhere to our Terms of Use if you use dyn-web code.';
    if ( !( loc == '' || loc == '127.0.0.1' || loc.indexOf('localhost') != -1 
         || loc.indexOf('192.168.') != -1 || loc.indexOf('dyn-web.com') != -1 ) ) {
        alert(msg);
    }
}
dw_Event.add( window, 'load', dw_checkAuth);
/////////////////////////////////////////////////////////////////////