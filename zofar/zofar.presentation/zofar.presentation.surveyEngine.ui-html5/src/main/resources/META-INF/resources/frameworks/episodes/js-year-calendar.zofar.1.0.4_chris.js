(function (global, factory) {
  if (typeof define === "function" && define.amd) {
    define(["exports"], factory);
  } else if (typeof exports !== "undefined") {
    factory(exports);
  } else {
    var mod = {
      exports: {}
    };
    factory(mod.exports);
    global.jsYearCalendar = mod.exports;
  }
})(typeof globalThis !== "undefined" ? globalThis : typeof self !== "undefined" ? self : this, function (_exports) {
  "use strict";

  Object.defineProperty(_exports, "__esModule", {
    value: true
  });
  _exports["default"] = void 0;

  function _typeof(obj) { "@babel/helpers - typeof"; if (typeof Symbol === "function" && typeof Symbol.iterator === "symbol") { _typeof = function _typeof(obj) { return typeof obj; }; } else { _typeof = function _typeof(obj) { return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; }; } return _typeof(obj); }

  function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

  function _defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } }

  function _createClass(Constructor, protoProps, staticProps) { if (protoProps) _defineProperties(Constructor.prototype, protoProps); if (staticProps) _defineProperties(Constructor, staticProps); return Constructor; }

  function _defineProperty(obj, key, value) {
      if (key in obj)
      { Object.defineProperty(obj, key,
          { value: value, enumerable: true, configurable: true, writable: true });
      } else {
          obj[key] = value;
      } return obj;
  }

  if (typeof NodeList !== "undefined" && !NodeList.prototype.forEach) {
    NodeList.prototype.forEach = function (callback, thisArg) {
      thisArg = thisArg || window;

      for (var i = 0; i < this.length; i++) {
        callback.call(thisArg, this[i], i, this);
      }
    };
  }

  if (typeof Element !== "undefined" && !Element.prototype.matches) {
    var prototype = Element.prototype;
    Element.prototype.matches = prototype.msMatchesSelector || prototype.webkitMatchesSelector;
  }

  if (typeof Element !== "undefined" && !Element.prototype.closest) {
    Element.prototype.closest = function (s) {
      var el = this;
      if (!document.documentElement.contains(el)) return null;

      do {
        if (el.matches(s)) return el;
        el = el.parentElement || el.parentNode;
      } while (el !== null && el.nodeType == 1);

      return null;
    };
  }

  var Calendar = function () {
    function Calendar(element) {
      var options = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : null;

      _classCallCheck(this, Calendar);

      _defineProperty(this, "element", void 0);

      _defineProperty(this, "options", void 0);

      _defineProperty(this, "_dataSource", void 0);

      _defineProperty(this, "_mouseDown", void 0);

      _defineProperty(this, "_rangeStart", void 0);

      _defineProperty(this, "_rangeEnd", void 0);

      _defineProperty(this, "_responsiveInterval", void 0);

      _defineProperty(this, "_nbCols", void 0);

      _defineProperty(this, "clickDay", void 0);

      _defineProperty(this, "dayContextMenu", void 0);

      _defineProperty(this, "mouseOnDay", void 0);

      _defineProperty(this, "mouseOutDay", void 0);

      _defineProperty(this, "renderEnd", void 0);

      _defineProperty(this, "selectRange", void 0);

      _defineProperty(this, "yearChanged", void 0);

      if (element instanceof HTMLElement) {
        this.element = element;
      } else if (typeof element === "string") {
        this.element = document.querySelector(element);
      } else {
        throw new Error("The element parameter should be a DOM node or a selector");
      }

      this.element.classList.add('calendar');

      this._initializeEvents(options);

      this._initializeOptions(options);
      //console.log("min date "+options.minDate+" ("+isNaN(options.minDate.getTime())+")");
      //console.log("max date "+options.maxDate+" ("+isNaN(options.maxDate.getTime())+")");

      this.setYear(this.options.startYear);

      console.log(this.options);
      //console.log(this.element);
    }

    _createClass(Calendar, [
    	{
    	      key: "_adjustDate",
    	      value: function _adjustDate(date) {
    	        if (date == null) {
    	          return null;
    	        }
    	        //console.log(date.getFullYear()+'-'+(date.getMonth() < 9 ? '0': '') + (date.getMonth()+1)+'-'+(date.getDate() < 9 ? '0': '') +date.getDate()+'T0'+(((date.getTimezoneOffset()/60)*(-1))+1)+':00:00Z');
//    	        var adjustedDate = new Date(date.getFullYear()+'-'+(date.getMonth() < 9 ? '0': '') + (date.getMonth()+1)+'-'+(date.getDate() < 9 ? '0': '') +date.getDate()+'T0'+(((date.getTimezoneOffset()/60)*(-1))+1)+':00:00Z');
    	        var adjustedDate = new Date(date.getFullYear()+'-'+(date.getMonth() < 9 ? '0': '') + (date.getMonth()+1)+'-'+(date.getDate() < 9 ? '0': '') +date.getDate()+'T01:00:00Z');
    	        //console.log("adjust : "+date+" => "+adjustedDate)
    	        return adjustedDate;
    	      }
    },{
      key: "_initializeOptions",
      value: function _initializeOptions(opt) {
        if (opt == null) {
          opt = {};
        }
        this.options = {
          startYear: !isNaN(parseInt(opt.startYear)) ? parseInt(opt.startYear) : new Date().getFullYear(),
          minDate: opt.minDate instanceof Date ? opt.minDate : null,
          maxDate: opt.maxDate instanceof Date ? opt.maxDate : null,
          language: opt.language != null && Calendar.locales[opt.language] != null ? opt.language : 'en',
          allowOverlap: opt.allowOverlap != null ? opt.allowOverlap : true,
          displayWeekNumber: opt.displayWeekNumber != null ? opt.displayWeekNumber : false,
          displayDisabledDataSource: opt.displayDisabledDataSource != null ? opt.displayDisabledDataSource : false,
          displayHeader: opt.displayHeader != null ? opt.displayHeader : true,
          alwaysHalfDay: opt.alwaysHalfDay != null ? opt.alwaysHalfDay : false,
          enableRangeSelection: opt.enableRangeSelection != null ? opt.enableRangeSelection : false,
          disabledDays: opt.disabledDays instanceof Array ? opt.disabledDays : [],
          disabledWeekDays: opt.disabledWeekDays instanceof Array ? opt.disabledWeekDays : [],
          hiddenWeekDays: opt.hiddenWeekDays instanceof Array ? opt.hiddenWeekDays : [],
          roundRangeLimits: opt.roundRangeLimits != null ? opt.roundRangeLimits : false,
          dataSource: opt.dataSource instanceof Array || typeof opt.dataSource === "function" ? opt.dataSource : [],
          style: opt.style == 'background' || opt.style == 'border' || opt.style == 'custom' ? opt.style : 'border',
          enableContextMenu: opt.enableContextMenu != null ? opt.enableContextMenu : false,
          contextMenuItems: opt.contextMenuItems instanceof Array ? opt.contextMenuItems : [],
          customDayRenderer: typeof opt.customDayRenderer === "function" ? opt.customDayRenderer : null,
          customDataSourceRenderer: typeof opt.customDataSourceRenderer === "function" ? opt.customDataSourceRenderer : null,
          weekStart: !isNaN(parseInt(opt.weekStart)) ? parseInt(opt.weekStart) : null,
          loadingTemplate: typeof opt.loadingTemplate === "string" || opt.loadingTemplate instanceof HTMLElement ? opt.loadingTemplate : null,
          disabledData: opt.disabledData != null ? opt.disabledData : '{}',
          focusData: opt.focusData != null ? opt.focusData : ''
        };
        //console.log(this.options);
        if (this.options.dataSource instanceof Array) {
          this._dataSource = this.options.dataSource;

          this._zofarTypedInitializeDatasourceColors();
        }
      }
      }, {
      key: "_initializeEvents",
      value: function _initializeEvents(opt) {
        if (opt == null) {
          opt = [];
        }

        if (opt.yearChanged) {
          this.element.addEventListener('yearChanged', opt.yearChanged);
        }

        if (opt.renderEnd) {
          this.element.addEventListener('renderEnd', opt.renderEnd);
        }

        if (opt.clickDay) {
          this.element.addEventListener('clickDay', opt.clickDay);
        }

        if (opt.dayContextMenu) {
          this.element.addEventListener('dayContextMenu', opt.dayContextMenu);
        }

        if (opt.selectRange) {
          this.element.addEventListener('selectRange', opt.selectRange);
        }

        if (opt.mouseOnDay) {
          this.element.addEventListener('mouseOnDay', opt.mouseOnDay);
        }

        if (opt.mouseOutDay) {
          this.element.addEventListener('mouseOutDay', opt.mouseOutDay);
        }
      }
    },
    {
      key: "_fetchDataSource",
      value: function _fetchDataSource(callback) {
        if (typeof this.options.dataSource === "function") {
          var getDataSource = this.options.dataSource;

          if (getDataSource.length == 2) {
            // 2 parameters, means callback method
            getDataSource(this.options.startYear, callback);
          } else {
            // 1 parameter, means synchronous or promise method
            var result = getDataSource(this.options.startYear);

            if (result instanceof Array) {
              callback(result);
            } else {
              result.then(callback);
            }
          }
        } else {
          callback(this.options.dataSource);
        }
      }
    },
    {
        key: "_zofarTypedInitializeDatasourceColors",
        value: function _zofarTypedInitializeDatasourceColors() {
        	//this._zofarInitializeDatasourceColors();
        	const typeColorFactors = new Array(-100,-75,-50,-25,0,25,50,75,100);
      	    for (var i = 0; i < this._dataSource.length; i++) {
      	        var episode = this._dataSource[i];
      	        if (episode.color == null) {
      	          const timezone = "Europe/Berlin";
      	          var episodeStartMoment = moment([episode.startDate.getFullYear(), episode.startDate.getMonth() , 1]);
      	          var episodeStart = new Date(episodeStartMoment.tz(timezone).format("YYYY-MM-DDT00:00:00Z")); 
      	          var episodeBeforeStartMoment = moment(episodeStart).subtract(1 , 'day').add(3 , 'hour');
      	          var episodeBeforeStart = new Date(episodeBeforeStartMoment.tz(timezone).format("YYYY-MM-DDT00:00:00Z"));
      	          var episodeEndMoment = moment([episode.endDate.getFullYear(), episode.endDate.getMonth(), 1]).endOf('month');
      	          var episodeEnd = new Date(episodeEndMoment.tz(timezone).format("YYYY-MM-DDT00:00:00Z"));
      	          var episodeAfterEndMoment = moment(episodeEnd).add(1 , 'day').add(3 , 'hour');
      	          var episodeAfterEnd = new Date(episodeAfterEndMoment.tz(timezone).format("YYYY-MM-DDT00:00:00Z"));
      	          var typeColors = new Array(0);
      	          
      	          //Convert color to rgb
	      	      var cvs = document.createElement('canvas');
	      	      cvs.height = 1;
	      	      cvs.width = 1;
	      	      var ctx = cvs.getContext('2d');
	      	      ctx.fillStyle = episode.typeColor;
	      	      ctx.fillRect(0, 0, 1, 1);
      	          var rgbColor = ctx.getImageData(0, 0, 1, 1).data;
      	          var tmpColor = (rgbColor.length >= 3) ? '#' + (((1 << 24) + (rgbColor[0] << 16) + (rgbColor[1] << 8) + rgbColor[2]).toString(16).substr(1)) : false;

      	          //calculate variants of base color
      	          for (var colorIndex = 0; colorIndex < typeColorFactors.length; colorIndex++) {
      	        	  	var col = tmpColor;
      	        	  	var amt = typeColorFactors[colorIndex];
      	        		var usePound = false;
	        		  
	        		    if (col[0] == "#") {
	        		        col = col.slice(1);
	        		        usePound = true;
	        		    }
	        		 
	        		    var num = parseInt(col,16);
	        		 
	        		    var r = (num >> 16) + amt;
	        		 
	        		    if (r > 255) r = 255;
	        		    else if  (r < 0) r = 0;
	        		 
	        		    var b = ((num >> 8) & 0x00FF) + amt;
        		 
	        		    if (b > 255) b = 255;
	        		    else if  (b < 0) b = 0;
	        		 
	        		    var g = (num & 0x0000FF) + amt;
	        		 
	        		    if (g > 255) g = 255;
	        		    else if (g < 0) g = 0;
	        		    
	        		    var calculatedColor = (usePound?"#":"") + (g | (b << 8) | (r << 16)).toString(16);
	        		    typeColors.push(calculatedColor);
      	          }

      	          var possibleColors = typeColors;
      	          for (var j = 0; j < this._dataSource.length; j++) {
      	              var checkEpisode = this._dataSource[j];
      	              if (i == j) continue;
      	              var checkEpisodeStart = new Date(checkEpisode.startDate.getFullYear(), checkEpisode.startDate.getMonth() , 1);
      	              var checkEpisodeEnd = new Date(checkEpisode.endDate.getFullYear(), checkEpisode.endDate.getMonth() + 1 , 0);
      	              if (checkEpisode.color == null) continue;

      	              var relevant = false;
      	              if(episodeBeforeStart.getTime() == checkEpisodeEnd.getTime()){
      	            	  relevant = true;
      	              }
      	              if(episodeAfterEnd.getTime() == checkEpisodeStart.getTime()){
      	            	  relevant = true;
      	              }
      	              if((episodeStart.getTime() >= checkEpisodeStart.getTime())&&(episodeStart.getTime() <= checkEpisodeEnd.getTime())){
      	            	  relevant = true;
      	              }
      	              if((episodeEnd.getTime() >= checkEpisodeStart.getTime())&&(episodeEnd.getTime() <= checkEpisodeEnd.getTime())){
        	            	  relevant = true;
      	              }
      	              if(relevant){
      	                  for( var c = 0; c < possibleColors.length; c++){ if ( possibleColors[c] === checkEpisode.color) { possibleColors.splice(c, 1); }}
      	              }
      	          }

      	          if(possibleColors.length == 0){
      	        	  //possibleColors = typeColors;
      	        	possibleColors.push('#000000');
      	          }

      	          let random = Math.floor(Math.random()*(possibleColors.length-1));
      	          //this._dataSource[i].color = possibleColors[random];
      	          this._dataSource[i].color = episode.typeColor;
      	        }
      	    }
        }
      },
    {
        key: "_zofarInitializeDatasourceColors",
        value: function _zofarInitializeDatasourceColors() {
 	  	
      	    for (var i = 0; i < this._dataSource.length; i++) {
      	        var episode = this._dataSource[i];
      	        if (episode.color == null) {
      	          const timezone = "Europe/Berlin";
      	          var episodeStartMoment = moment([episode.startDate.getFullYear(), episode.startDate.getMonth() , 1]);
      	          var episodeStart = new Date(episodeStartMoment.tz(timezone).format("YYYY-MM-DDT00:00:00Z")); 
      	          var episodeBeforeStartMoment = moment(episodeStart).subtract(1 , 'day').add(3 , 'hour');
      	          var episodeBeforeStart = new Date(episodeBeforeStartMoment.tz(timezone).format("YYYY-MM-DDT00:00:00Z"));
      	          var episodeEndMoment = moment([episode.endDate.getFullYear(), episode.endDate.getMonth(), 1]).endOf('month');
      	          var episodeEnd = new Date(episodeEndMoment.tz(timezone).format("YYYY-MM-DDT00:00:00Z"));
      	          var episodeAfterEndMoment = moment(episodeEnd).add(1 , 'day').add(3 , 'hour');
      	          var episodeAfterEnd = new Date(episodeAfterEndMoment.tz(timezone).format("YYYY-MM-DDT00:00:00Z"));
      	          var possibleColors = Calendar.colors;

      	          for (var j = 0; j < this._dataSource.length; j++) {
      	              var checkEpisode = this._dataSource[j];
      	              if (i == j) continue;
      	              var checkEpisodeStart = new Date(checkEpisode.startDate.getFullYear(), checkEpisode.startDate.getMonth() , 1);
      	              var checkEpisodeEnd = new Date(checkEpisode.endDate.getFullYear(), checkEpisode.endDate.getMonth() + 1 , 0);
      	              if (checkEpisode.color == null) continue;

      	              var relevant = false;
      	              if(episodeBeforeStart.getTime() == checkEpisodeEnd.getTime()){
      	            	  relevant = true;
      	              }
      	              if(episodeAfterEnd.getTime() == checkEpisodeStart.getTime()){
      	            	  relevant = true;
      	              }
      	              if((episodeStart.getTime() >= checkEpisodeStart.getTime())&&(episodeStart.getTime() <= checkEpisodeEnd.getTime())){
      	            	  relevant = true;
      	              }
      	              if((episodeEnd.getTime() >= checkEpisodeStart.getTime())&&(episodeEnd.getTime() <= checkEpisodeEnd.getTime())){
        	            	  relevant = true;
      	              }
      	              if(relevant){
      	                  for( var c = 0; c < possibleColors.length; c++){ if ( possibleColors[c] === checkEpisode.color) { possibleColors.splice(c, 1); }}
      	              }
      	          }
      	          if(possibleColors.length == 0)possibleColors = Calendar.colors;
      	          if(possibleColors.length == 1){
      	        		  var col = possibleColors[0];
      	        		  var amt = -40;
      	        		    var usePound = false;
      	        		  
      	        		    if (col[0] == "#") {
      	        		        col = col.slice(1);
      	        		        usePound = true;
      	        		    }
      	        		 
      	        		    var num = parseInt(col,16);
      	        		 
      	        		    var r = (num >> 16) + amt;
      	        		 
      	        		    if (r > 255) r = 255;
      	        		    else if  (r < 0) r = 0;
      	        		 
      	        		    var b = ((num >> 8) & 0x00FF) + amt;
      	        		 
      	        		    if (b > 255) b = 255;
      	        		    else if  (b < 0) b = 0;
      	        		 
      	        		    var g = (num & 0x0000FF) + amt;
      	        		 
      	        		    if (g > 255) g = 255;
      	        		    else if (g < 0) g = 0;
      	        		 
      	        		    possibleColors[0] =  (usePound?"#":"") + (g | (b << 8) | (r << 16)).toString(16);
      	          }
      	          let random = Math.floor(Math.random()*(possibleColors.length-1));
      	          //this._dataSource[i].color = possibleColors[random];
      	          this._dataSource[i].color = episode.typeColor;
      	        }
      	    }
          }
      },
    {
      key: "_initializeDatasourceColors",
      value: function _initializeDatasourceColors() { 
	       for (var i = 0; i < this._dataSource.length; i++) {
	          if (this._dataSource[i].color == null) {
	            this._dataSource[i].color = Calendar.colors[i % Calendar.colors.length];
	          }
	        }     
      }
      /**
          * Renders the calendar.
          */

    }, {
      key: "render",
      value: function render() {
          var isLoading = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : false;
	//console.log("debug1");
          // Clear the calendar (faster method)
          while (this.element.firstChild) {
              this.element.removeChild(this.element.firstChild);
          }

          if (isLoading) {
              this._renderLoading();
          } else {
              if ($(window).width() > 768) {
		  //console.log("debug2");
                  this._zofarResponsiveRenderBody();
              } else {
		 //console.log("debug3");
                  this._zofarMobileRenderBody();
              }
              this._zofarResponsiveRenderDataSource();

              this._zofarApplyEvents(); // Fade animation

              this._triggerEvent('renderEnd', {
                  currentYear: this.options.startYear
              });
          }
      }
    },
    {   key: "_zofarResponsiveRenderBody",
        value: function _zofarResponsiveRenderBody() {
        	var coreElement = "div";
            var calendarContainer = document.createElement('div')
            calendarContainer.id = 'calenderTable';
        	var yearsContainer = document.createElement('table');
        	yearsContainer.classList.add('zofar-structure-table');
        	yearsContainer.classList.add('years-container');
        	yearsContainer.classList.add('zofar-responsive');

        	var yearsContainerHead = document.createElement('thead');
        	yearsContainerHead.classList.add('years-container-head');

        	var headRow = document.createElement('tr');
        	headRow.classList.add('zofar-structure-table-row');

            var blankHeadCell = document.createElement('th');
            blankHeadCell.classList.add('zofar-structure-table-head-cell');
            var blankHeadCellLabel = document.createElement('span');
            blankHeadCellLabel.classList.add('head-blank');
            blankHeadCellLabel.textContent = "";
            blankHeadCell.appendChild(blankHeadCellLabel);
            headRow.appendChild(blankHeadCell);

            for (var m = 1; m <= 12; m++) {
                var monthHeadCell = document.createElement('th');
                monthHeadCell.classList.add('zofar-structure-table-head-cell');

                var monthHeadCellLabel = document.createElement('span');
                monthHeadCellLabel.classList.add('month-header');
                monthHeadCellLabel.classList.add('month-header'.concat(m.toString()));
                var monthTitle = Calendar.locales[this.options.language].months[m-1];
                monthHeadCellLabel.textContent=monthTitle;
                monthHeadCell.appendChild(monthHeadCellLabel);
                monthHeadCell.dataset.monthTitle = monthTitle;
                headRow.appendChild(monthHeadCell);
            }

        	yearsContainerHead.appendChild(headRow);
        	yearsContainer.appendChild(yearsContainerHead);

        	var yearsContainerBody = document.createElement('tbody');
        	yearsContainerBody.classList.add('years-container-body');

	        var startY = this.options.minDate.getFullYear()+0;
	        var stopY = this.options.maxDate.getFullYear()+0;
		//console.log("startY "+startY+" stopY "+stopY);
	        
		for(var y = startY; y <= stopY;y++){
	        	var yearRow = document.createElement('tr');
	        	yearRow.classList.add('zofar-structure-table-row');
	        	yearRow.classList.add('year-container');
	        	yearRow.classList.add('zofar-months-container');
	        	yearRow.classList.add('year-container-'.concat(y.toString()));
	        	yearRow.dataset.yearId = y.toString();

                var yearLabelCell = document.createElement('td');
                yearLabelCell.classList.add('zofar-structure-table-body-cell');
	            yearLabelCell.classList.add('zofar-year-cell');
	            var yearLabelCellLabel = document.createElement('span');
	            yearLabelCellLabel.classList.add('year-label');
	            yearLabelCellLabel.textContent = y.toString();
	            yearLabelCellLabel.dataset.yearId = y.toString();
	            yearLabelCell.appendChild(yearLabelCellLabel);
	            yearRow.appendChild(yearLabelCell);

	            for (var m = 1; m <= 12; m++) {
	                var monthBodyCell = document.createElement('td');
	                monthBodyCell.classList.add('zofar-structure-table-body-cell');
	                monthBodyCell.classList.add('month');
	                monthBodyCell.classList.add('zofar-month');
	                monthBodyCell.classList.add('zofar-month-'.concat(m.toString()));

	                if (this._isZofarDisabled(m,y)) {
	                	monthBodyCell.classList.add('zofar-month-disabled');
	                }

	                monthBodyCell.dataset.monthId = m.toString();
	                monthBodyCell.dataset.yearId = y.toString();

	            	var monthTitle = Calendar.locales[this.options.language].months[m-1];
	            	monthBodyCell.dataset.monthTitle = monthTitle;

	                yearRow.appendChild(monthBodyCell);
	            }
	        	yearsContainerBody.appendChild(yearRow);
	        }
        	yearsContainer.appendChild(yearsContainerBody);
            calendarContainer.appendChild(yearsContainer)
        	this.element.appendChild(calendarContainer);
        }
      },
    {   key: "_zofarMobileRenderBody",
            value: function _zofarMobileRenderBody() {
                var calendarContainer = document.createElement('div')
                calendarContainer.id = 'calenderTable';
                var yearsContainer = document.createElement('table');
                yearsContainer.classList.add('zofar-structure-table');
            	yearsContainer.classList.add('years-container');
            	yearsContainer.classList.add('zofar-responsive');
            	yearsContainer.classList.add('table');
            	yearsContainer.classList.add('table-bordered');

            	var yearsContainerBody = document.createElement('tbody');
            	yearsContainerBody.classList.add('years-container-body');

    	        var startY = this.options.minDate.getFullYear()+0;
    	        var stopY = this.options.maxDate.getFullYear()+0;

    	        for(var y = startY; y <= stopY;y++){
    	        	var yearRow = document.createElement('tr');
    	        	var yearTitles = document.createElement('td');
    	        	yearTitles.classList.add('year-titles');
    	        	yearTitles.textContent = y;
    	        	yearRow.appendChild(yearTitles);
    	        	yearRow.classList.add('zofar-structure-table-row');
    	        	yearRow.classList.add('year-container');
    	        	yearRow.classList.add('zofar-months-container');
    	        	yearRow.classList.add('year-container-'.concat(y.toString()));
    	        	yearRow.dataset.yearId = y.toString();

                    var yearLabelCell = document.createElement('td');
                    yearLabelCell.classList.add('zofar-structure-table-body-cell');
    	            yearLabelCell.classList.add('zofar-year-cell');
    	            var yearLabelCellLabel = document.createElement('span');
    	            yearLabelCellLabel.classList.add('year-label');
    	            yearLabelCellLabel.textContent = y.toString();
    	            yearLabelCellLabel.dataset.yearId = y.toString();
    	            yearLabelCell.appendChild(yearLabelCellLabel);
    	            yearRow.appendChild(yearLabelCell);
                    let monthId = 0;
    	            for (var i = 0; i < 4; i++) {
    	                var seasonTd = document.createElement('td');
    	                seasonTd.classList.add('season');

        	            for (var j = 0; j < 3; j++) {
/*				    TODO: Reihenfolge Monate*/
				    /* monthId++;*/
				    monthId=i+j*4+1;

        	               var monthBodyCell = document.createElement('td');
                           monthBodyCell.classList.add('zofar-structure-table-body-cell');
                           monthBodyCell.classList.add('month');
                           monthBodyCell.classList.add('zofar-month');
                           monthBodyCell.dataset.year = y.toString();
                           monthBodyCell.dataset.id = monthId;
                           monthBodyCell.dataset.monthId = Calendar.locales[this.options.language].monthsShort[monthId -1];
                           seasonTd.appendChild(monthBodyCell);
        	            }
    	                yearRow.appendChild(seasonTd);
    	            }
    	        	yearsContainerBody.appendChild(yearRow);
    	        }
            	yearsContainer.appendChild(yearsContainerBody);
            	this.element.classList.add('table-responsive');
                calendarContainer.appendChild(yearsContainer)
            	this.element.appendChild(calendarContainer);
            }
          },
      {   key: "_isZofarDisabled",
          value: function _isZofarDisabled(m,y) {
        	  var disabledYear = this.options.disabledData[y];
        	  if(disabledYear != null){
        		  if(disabledYear.includes(m))return true;
        	  }
        	  return false;
          }
      },
    {   key: "_zofarRenderBody",
        value: function _zofarRenderBody() {
          var yearsDiv = document.createElement('div');
          yearsDiv.classList.add('years-container');
          var startY = this.options.startYear+0;
          for(var y = startY; y < startY+3;y++){
              var yearDiv = document.createElement('span');
              yearDiv.classList.add('year-container');
              yearDiv.classList.add('year');
              yearDiv.classList.add('year-container-'.concat(y.toString()));
              yearDiv.dataset.yearId = y.toString();

              var yearTable = document.createElement('table');

	              var yearTh = document.createElement('tr');
	              yearTh.classList.add('mainHeader');
	              var monthTh = document.createElement('th');
	              var monthLabelX = document.createElement('span');
	              monthLabelX.classList.add('month-label-test');
	              monthLabelX.classList.add('year-title');
	              monthLabelX.textContent = "";
	              monthTh.appendChild(monthLabelX);
	              yearTh.appendChild(monthTh);
		          
	              var monthSpanTh = document.createElement('th');
	              monthSpanTh.classList.add('mainHeader-month');
	              for (var m = 1; m <= 12; m++) {
	                  var monthDiv = document.createElement('div');
	                  monthDiv.classList.add('month-header');
	                  monthDiv.classList.add('month-header'.concat(m.toString()));
	                  // TODO 
	                  var monthLabel = document.createElement('div');
	                  monthLabel.classList.add('month-label-test');
	                  
	                  var monthTitle = Calendar.locales[this.options.language].months[m-1];
	                  monthLabel.textContent=monthTitle;
	                  
	                  monthDiv.appendChild(monthLabel)
	
	                  monthSpanTh.appendChild(monthDiv);
	                  yearTh.appendChild(monthSpanTh);
	                }
	              yearTable.appendChild(yearTh);
	              
              var yearRow = document.createElement('tr');
              
	          var yearTitle= document.createElement('td');
	          yearTitle.classList.add('year-title');
	          yearTitle.textContent = y.toString();
	          yearRow.appendChild(yearTitle);
	          
	          var yearContent= document.createElement('td');
	          yearContent.classList.add('year-content');
	          yearContent.classList.add('months');
	          yearContent.classList.add('months-container');
              for (var m = 1; m <= 12; m++) {
                var monthDiv = document.createElement('span');
                monthDiv.classList.add('month');
                monthDiv.classList.add('zofar-month');
                monthDiv.classList.add('zofar-month-'.concat(m.toString()));
                monthDiv.dataset.monthId = m.toString(); 
                monthDiv.dataset.yearId = y.toString();
                
                if (this._isZofarDisabled(m,y)) {
                	monthDiv.classList.add('zofar-month-disabled');
                }
                
                // TODO VD
                var monthLabel = document.createElement('div');
                monthLabel.classList.add('month-label');
                
                var monthTitle = Calendar.locales[this.options.language].months[m-1];

                monthDiv.dataset.monthTitle = monthTitle;

                monthDiv.appendChild(monthLabel);
                yearContent.appendChild(monthDiv);
              }
              yearRow.appendChild(yearContent);
              yearTable.appendChild(yearRow);
              yearDiv.appendChild(yearTable);
              yearsDiv.appendChild(yearDiv);
          }
          this.element.appendChild(yearsDiv);
        }
      },
    {
      key: "_renderHeader",
      value: function _renderHeader() {
        var header = document.createElement('div');
        header.classList.add('calendar-header');
        var headerTable = document.createElement('table');
        var prevDiv = document.createElement('th');
        prevDiv.classList.add('prev');

        if (this.options.minDate != null && this.options.minDate > new Date(this.options.startYear - 1, 11, 31)) {
          prevDiv.classList.add('disabled');
        }

        var prevIcon = document.createElement('span');
        prevIcon.innerHTML = "&lsaquo;";
        prevDiv.appendChild(prevIcon);
        headerTable.appendChild(prevDiv);
        var prev2YearDiv = document.createElement('th');
        prev2YearDiv.classList.add('year-title');
        prev2YearDiv.classList.add('year-neighbor2');
        prev2YearDiv.textContent = (this.options.startYear - 2).toString();

        if (this.options.minDate != null && this.options.minDate > new Date(this.options.startYear - 2, 11, 31)) {
          prev2YearDiv.classList.add('disabled');
        }

        headerTable.appendChild(prev2YearDiv);
        var prevYearDiv = document.createElement('th');
        prevYearDiv.classList.add('year-title');
        prevYearDiv.classList.add('year-neighbor');
        prevYearDiv.textContent = (this.options.startYear - 1).toString();

        if (this.options.minDate != null && this.options.minDate > new Date(this.options.startYear - 1, 11, 31)) {
          prevYearDiv.classList.add('disabled');
        }

        headerTable.appendChild(prevYearDiv);
        var yearDiv = document.createElement('th');
        yearDiv.classList.add('year-title');
        yearDiv.textContent = this.options.startYear.toString();
        headerTable.appendChild(yearDiv);
        var nextYearDiv = document.createElement('th');
        nextYearDiv.classList.add('year-title');
        nextYearDiv.classList.add('year-neighbor');
        nextYearDiv.textContent = (this.options.startYear + 1).toString();

        if (this.options.maxDate != null && this.options.maxDate < new Date(this.options.startYear + 1, 0, 1)) {
          nextYearDiv.classList.add('disabled');
        }

        headerTable.appendChild(nextYearDiv);
        var next2YearDiv = document.createElement('th');
        next2YearDiv.classList.add('year-title');
        next2YearDiv.classList.add('year-neighbor2');
        next2YearDiv.textContent = (this.options.startYear + 2).toString();

        if (this.options.maxDate != null && this.options.maxDate < new Date(this.options.startYear + 2, 0, 1)) {
          next2YearDiv.classList.add('disabled');
        }

        headerTable.appendChild(next2YearDiv);
        var nextDiv = document.createElement('th');
        nextDiv.classList.add('next');

        if (this.options.maxDate != null && this.options.maxDate < new Date(this.options.startYear + 1, 0, 1)) {
          nextDiv.classList.add('disabled');
        }

        var nextIcon = document.createElement('span');
        nextIcon.innerHTML = "&rsaquo;";
        nextDiv.appendChild(nextIcon);
        headerTable.appendChild(nextDiv);
        header.appendChild(headerTable);
        this.element.appendChild(header);
      }
    }, {
      key: "_renderBody",
      value: function _renderBody() {
        var monthsDiv = document.createElement('div');
        monthsDiv.classList.add('months-container');

        for (var m = 0; m < 12; m++) {
          /* Container */
          var monthDiv = document.createElement('div');
          monthDiv.classList.add('month-container');
          monthDiv.dataset.monthId = m.toString();

          if (this._nbCols) {
            monthDiv.classList.add("month-".concat(this._nbCols));
          }

          var firstDate = new Date(this.options.startYear, m, 1);
          var table = document.createElement('table');
          table.classList.add('month');
          /* Month header */

          var thead = document.createElement('thead');
          var titleRow = document.createElement('tr');
          var titleCell = document.createElement('th');
          titleCell.classList.add('month-title');
          titleCell.setAttribute('colspan', this.options.displayWeekNumber ? '8' : '7');
          titleCell.textContent = Calendar.locales[this.options.language].months[m];
          titleRow.appendChild(titleCell);
          thead.appendChild(titleRow);
          var headerRow = document.createElement('tr');

          if (this.options.displayWeekNumber) {
            var weekNumberCell = document.createElement('th');
            weekNumberCell.classList.add('week-number');
            weekNumberCell.textContent = Calendar.locales[this.options.language].weekShort;
            headerRow.appendChild(weekNumberCell);
          }

          var weekStart = this.options.weekStart ? this.options.weekStart : Calendar.locales[this.options.language].weekStart;
          var d = weekStart;

          do {
            var headerCell = document.createElement('th');
            headerCell.classList.add('day-header');
            headerCell.textContent = Calendar.locales[this.options.language].daysMin[d];

            if (this._isHidden(d)) {
              headerCell.classList.add('hidden');
            }

            headerRow.appendChild(headerCell);
            d++;
            if (d >= 7) d = 0;
          } while (d != weekStart);

          thead.appendChild(headerRow);
          table.appendChild(thead);
          /* Days */

          var currentDate = new Date(firstDate.getTime());
          var lastDate = new Date(this.options.startYear, m + 1, 0);

          while (currentDate.getDay() != weekStart) {
            currentDate.setDate(currentDate.getDate() - 1);
          }

          while (currentDate <= lastDate) {
            var row = document.createElement('tr');

            if (this.options.displayWeekNumber) {
              var weekNumberCell = document.createElement('td');
              var currentThursday = new Date(currentDate.getTime()); // Week number is computed based on the thursday

              currentThursday.setDate(currentThursday.getDate() - weekStart + 4);
              weekNumberCell.classList.add('week-number');
              weekNumberCell.textContent = this.getWeekNumber(currentThursday).toString();
              row.appendChild(weekNumberCell);
            }

            do {
              var cell = document.createElement('td');
              cell.classList.add('day');

              if (this._isHidden(currentDate.getDay())) {
                cell.classList.add('hidden');
              }

              if (currentDate < firstDate) {
                cell.classList.add('old');
              } else if (currentDate > lastDate) {
                cell.classList.add('new');
              } else {
                if (this._isDisabled(currentDate)) {
                  cell.classList.add('disabled');
                }

                var cellContent = document.createElement('div');
                cellContent.classList.add('day-content');
                cellContent.textContent = currentDate.getDate().toString();
                cell.appendChild(cellContent);

                if (this.options.customDayRenderer) {
                  this.options.customDayRenderer(cellContent, currentDate);
                }
              }

              row.appendChild(cell);
              currentDate.setDate(currentDate.getDate() + 1);
            } while (currentDate.getDay() != weekStart);

            table.appendChild(row);
          }

          monthDiv.appendChild(table);
          monthsDiv.appendChild(monthDiv);
        }

        this.element.appendChild(monthsDiv);
      }
    }, {
      key: "_renderLoading",
      value: function _renderLoading() {
        var container = document.createElement('div');
        container.classList.add('calendar-loading-container');
        container.style.minHeight = this._nbCols * 200 + 'px';
        var loading = document.createElement('div');
        loading.classList.add('calendar-loading');

        if (this.options.loadingTemplate) {
          if (typeof this.options.loadingTemplate === "string") {
            loading.innerHTML = this.options.loadingTemplate;
          } else if (this.options.loadingTemplate instanceof HTMLElement) {
            loading.appendChild(this.options.loadingTemplate);
          }
        } else {
          var spinner = document.createElement('div');
          spinner.classList.add('calendar-spinner');

          for (var i = 1; i <= 3; i++) {
            var bounce = document.createElement('div');
            bounce.classList.add("bounce".concat(i));
            spinner.appendChild(bounce);
          }

          loading.appendChild(spinner);
        }

        container.appendChild(loading);
        this.element.appendChild(container);
      }
    }, 
    {
        key: "_zofarResponsiveRenderDataSource",
        value: function _zofarResponsiveRenderDataSource() {
          var _this = this;

          if (this._dataSource != null && this._dataSource.length > 0) {

            var slots = new Array(0);
            for (var i = 0; i < this._dataSource.length; i++) {
                var episode = this._dataSource[i];
                var savedSlot = episode.slot;
                if (savedSlot != null){
                	
                	while(slots.length <= savedSlot){
                        var slot = new Array(0);
                        slots.push(slot);
                	}
                	
                	slots[savedSlot].push(episode);
                	continue;
                }
                
                
                var episodeStart = new Date(episode.startDate.getFullYear(), episode.startDate.getMonth() , 1);
                var episodeEnd = new Date(episode.endDate.getFullYear(), episode.endDate.getMonth() + 1 , 0);

                var flag = false;
                for(var slotIndex = 0; slotIndex < slots.length;slotIndex++){
                    var fit = true;
                    for(var episodeIndex = 0; episodeIndex < slots[slotIndex].length;episodeIndex++){
                        var slotEpisode = slots[slotIndex][episodeIndex];
                        var slotEpisodeStart = new Date(slotEpisode.startDate.getFullYear(), slotEpisode.startDate.getMonth() , 1);
                        var slotEpisodeEnd = new Date(slotEpisode.endDate.getFullYear(), slotEpisode.endDate.getMonth() + 1 , 0);
                        if((episodeStart >=  slotEpisodeStart)&&(episodeStart <= slotEpisodeEnd))fit = false;
                        if((episodeEnd >=  slotEpisodeStart)&&(episodeEnd <= slotEpisodeEnd))fit = false;
                        if((episodeStart <=  slotEpisodeStart)&&(episodeEnd >= slotEpisodeEnd))fit = false;
                        if(!fit) break;
                    }
                    if(fit){
                      flag = true;
                      slots[slotIndex].push(episode);
                      episode.slot = slotIndex;
                      this._dataSource[i] = episode;
                      break;
                    }
                }

                if(!flag){
                    var slot = new Array(0);
                    slot.push(episode);
                    episode.slot = slots.length;
                    this._dataSource[i] = episode;
                    slots.push(slot);
                }
            }
        	  
            var cleanedSlots = new Array(0);
        	for (var i = 0; i < slots.length; i++) {
        		var slot = slots[i];
        		//console.log(slot);
        		if(slot.length == 0)continue;
        		cleanedSlots.push(slot);
        	}
        	slots = cleanedSlots;
        	  
            this.element.querySelectorAll('.zofar-month').forEach(function (month) {
              var monthId = 0;
              var yearId = 0;
              if (month.dataset.yearId === undefined){
                  monthId = parseInt(month.dataset.id-1);
                  yearId = parseInt(month.dataset.year);
              }else {
                  monthId = parseInt(month.dataset.monthId-1);
                  yearId = parseInt(month.dataset.yearId);
              }

              var firstDate = new Date(yearId, monthId, 1);
              var lastDate = new Date(yearId, monthId + 1, 0);
              
              //var debug = ((yearId==2021)&&(monthId < 5));
              var debug = true;

              if ((_this.options.minDate == null || lastDate > _this.options.minDate) && (_this.options.maxDate == null || firstDate <= _this.options.maxDate)) {
            	var monthData = new Map();
            	for (var i = 0; i < slots.length; i++) {
            		var slot = slots[i];
                	for (var j = 0; j < slot.length; j++) {
                    	var episodeStart = new Date(slot[j].startDate.getFullYear(), slot[j].startDate.getMonth() , 1);
                    	var episodeEnd = new Date(slot[j].endDate.getFullYear(), slot[j].endDate.getMonth() + 1 , 0);
         	               	
                    	var inRange = true;
                    	if(episodeStart > lastDate)inRange = false;
                    	if(firstDate > episodeEnd)inRange = false;
  	
                        if (inRange) {
                          monthData.set(i,slot[j]);
                        }
                        else{
                        	//monthData.set(i,null);
                        }
                	}
            	}
            	
                if(debug){
              	  //console.log(monthData);
                }
            	const slotLimit = 15;
               _this._zofarResponsiveRenderDataSourceMonth(month,monthData,slots.length,slotLimit);
              }
            });
          }
        }
      }, 
      {
      	  key: "_zofarRenderDataSourceMonth",
          value: function _zofarRenderDataSourceMonth(elt,events) {
            switch (this.options.style) {
              case 'border':
                var weight = 0;
                if (events.length == 1) {
                  weight = 4;
                } else if (events.length <= 3) {
                  weight = 2;
                } else {
                	elt.style.boxShadow = 'inset 0 -4px 0 0 black';
                }
                if (weight > 0) {
                  var boxShadow = '';

                  for (var i = 0; i < events.length; i++) {
                    if (boxShadow != '') {
                      boxShadow += ",";
                    }

                    boxShadow += "inset 0 -".concat((i + 1) * weight, "px 0 0 ").concat(events[i].color);
                  }

                  elt.style.boxShadow = boxShadow;
                }

                break;

              case 'background':
                break;

              case 'custom':
                break;
            }
          }
        }, 
        {
        	key: "_zofarResponsiveRenderDataSourceMonth",
            value: function _zofarResponsiveRenderDataSourceMonth(month,events,slotCount,slotLimit) {
              //console.log(month.dataset);
              var monthId = parseInt(month.dataset.monthId-1);
              var yearId = parseInt(month.dataset.yearId);
              
              var focusEpisodes = [];
              const parsed = this.options.focusData.trim().split(',');
              for (let i=0; i<parsed.length; i++) {
            	  const xx = parseInt(parsed[i].trim());
            	  if(isNaN(xx))continue;
            	  focusEpisodes.push(xx);
              }
              
              var hasFocus = false;
              if (typeof focusEpisodes != "undefined" && focusEpisodes != null && focusEpisodes.length != null && focusEpisodes.length > 0) {
            	    hasFocus = true;
              }

              for (var slot = 0; slot < Math.min(slotCount,slotLimit);slot++) {
	              var event = null;
	              event = events.get(Number(slot+""));
	        	  var color = "";
	        	  if((typeof event === 'undefined') || (event === null)){
	        		  color = "rgba(0, 0, 0, 0)";
	        	  }
	        	  else{
	        		  color = event.color;
		        	  if(hasFocus && !focusEpisodes.includes(event.id)){
		        		  color = event.color+";opacity:0.6;";
		        	  }
	        		  
	        	  }
	        	  

	        	  
	              var slotDiv = document.createElement("div");
	              slotDiv.setAttribute("data-slot", slot); 
	              slotDiv.setAttribute("style", "height:5px;display: flex;flex-direction: column;background-color:"+color+";"); 
	              var newContent = document.createElement("div");
	              newContent.setAttribute("style", "visibility: hidden;"); 
	              var xx = document.createTextNode(slot);
	              newContent.appendChild(xx);
	              slotDiv.appendChild(newContent);
	              month.appendChild(slotDiv);
              }
              var flag = false;
              if(events.size >= slotLimit) flag = true;
              for (var slot = slotLimit; slot < slotCount;slot++) {
	              var event = null;
	              event = events.get(Number(slot+"")-slotLimit);
	              if((typeof event === 'undefined') || (event === null))continue;
	              flag = true;
	              break;
              }
              
              //if(events.size >= slotLimit){
              if(flag){
            	  var limitDiv = null;
            	  for (var slot = slotLimit; slot < slotCount;slot++) {
    	              var event = null;
    	              event = events.get(Number(slot+""));
//    	              console.log(slot+" : "+event);
    	              if((typeof event === 'undefined') || (event === null))continue;
    	              if((typeof limitDiv === 'undefined') || (limitDiv === null)){
    	            	  limitDiv = document.createElement("div");
    	            	  limitDiv.setAttribute("data-more", ""); 
    	            	  limitDiv.setAttribute("style", "height:5px;display: flex;flex-direction: column;background-color:rgba(0, 0, 0, 0);"); 
    	            	  var newContent = document.createElement("span");
    	            	  //newContent.setAttribute("style", "visibility: hidden;"); 
    		              var xx = document.createTextNode("...");
    		              newContent.appendChild(xx);
    		              limitDiv.appendChild(newContent);
    	              }
    	              var more = limitDiv.getAttribute("data-more");
    	              more = more+event.id;
    	              limitDiv.setAttribute("data-more",more);
    	              
            	  }
	              if((typeof limitDiv === 'undefined') || (limitDiv === null)){

	              }else{
		              month.appendChild(limitDiv);
	              }
              }
              
//              
//	        	  var color = "";
//	        	  if(event === null){
//	        		  color = "rgba(0, 0, 0, 0)";
//	        	  }
//	        	  else{
//	        		  color = event.color;
//	        	  }
//	              var slotDiv = document.createElement("div");
//	              var newContent = document.createTextNode(slot);
//	              slotDiv.appendChild(newContent); // füge den Textknoten zum neu erstellten div hinzu.
//	              slotDiv.setAttribute("style", "background-color:"+color+";"); 
//	              month.appendChild(slotDiv); 
//              
//              }

//              var flag = false;
//              var boxShadow = '';
//              var weight = 6;
//              for (const [slot, event] of events.entries()) {
//            	  var color = "";
//            	  
//            	  if(event === null){
//            		  color = "rgba(0, 0, 0, 0)";
//            	  }
//            	  else{
//            		  color = event.color;
//            		  flag = true;
//            	  }
//            	  
//            	  if (boxShadow != '') {
//            		  boxShadow += ",";
//            	  }
//            	  
//            	  boxShadow += "inset 0 -".concat((slot + 1) * weight, "px ").concat(color);
//               }
//              if (boxShadow != '') {
//            	  month.setAttribute("style", "box-shadow:"+boxShadow+";"); 
//              }
            }
          },
    {
      key: "_renderDataSource",
      value: function _renderDataSource() {
        var _this = this;

        if (this._dataSource != null && this._dataSource.length > 0) {
          this.element.querySelectorAll('.month-container').forEach(function (month) {
            var firstDate = new Date(_this.options.startYear, monthId, 1);
            var lastDate = new Date(_this.options.startYear, monthId + 1, 1);

            if ((_this.options.minDate == null || lastDate > _this.options.minDate) && (_this.options.maxDate == null || firstDate <= _this.options.maxDate)) {
              var monthData = [];

              for (var i = 0; i < _this._dataSource.length; i++) {
                if (!(_this._dataSource[i].startDate >= lastDate) || _this._dataSource[i].endDate < firstDate) {
                  monthData.push(_this._dataSource[i]);
                }
              }

              if (monthData.length > 0) {
                month.querySelectorAll('.day-content').forEach(function (day) {
                  var currentDate = new Date(_this.options.startYear, monthId, parseInt(day.textContent));
                  var nextDate = new Date(_this.options.startYear, monthId, currentDate.getDate() + 1);
                  var dayData = [];

                  if ((_this.options.minDate == null || currentDate >= _this.options.minDate) && (_this.options.maxDate == null || currentDate <= _this.options.maxDate)) {
                    for (var i = 0; i < monthData.length; i++) {
                      if (monthData[i].startDate < nextDate && monthData[i].endDate >= currentDate) {
                        dayData.push(monthData[i]);
                      }
                    }

                    if (dayData.length > 0 && (_this.options.displayDisabledDataSource || !_this._isDisabled(currentDate))) {
                      _this._renderDataSourceDay(day, currentDate, dayData);
                    }
                  }
                });
              }
            }
          });
        }
      }
    }, {
      key: "_renderDataSourceDay",
      value: function _renderDataSourceDay(elt, currentDate, events) {
        var parent = elt.parentElement;

        switch (this.options.style) {
          case 'border':
            var weight = 0;

            if (events.length == 1) {
              weight = 4;
            } else if (events.length <= 3) {
              weight = 2;
            } else {
              parent.style.boxShadow = 'inset 0 -4px 0 0 black';
            }

            if (weight > 0) {
              var boxShadow = '';

              for (var i = 0; i < events.length; i++) {
                if (boxShadow != '') {
                  boxShadow += ",";
                }

                boxShadow += "inset 0 -".concat((i + 1) * weight, "px 0 0 ").concat(events[i].color);
              }

              parent.style.boxShadow = boxShadow;
            }

            break;

          case 'background':
            parent.style.backgroundColor = events[events.length - 1].color;
            var currentTime = currentDate.getTime();

            if (events[events.length - 1].startDate.getTime() == currentTime) {
              parent.classList.add('day-start');

              if (events[events.length - 1].startHalfDay || this.options.alwaysHalfDay) {
                parent.classList.add('day-half'); // Find color for other half

                var otherColor = 'transparent';

                for (var i = events.length - 2; i >= 0; i--) {
                  if (events[i].startDate.getTime() != currentTime || !events[i].startHalfDay && !this.options.alwaysHalfDay) {
                    otherColor = events[i].color;
                    break;
                  }
                }

                parent.style.background = "linear-gradient(-45deg, ".concat(events[events.length - 1].color, ", ").concat(events[events.length - 1].color, " 49%, ").concat(otherColor, " 51%, ").concat(otherColor, ")");
              } else if (this.options.roundRangeLimits) {
                parent.classList.add('round-left');
              }
            } else if (events[events.length - 1].endDate.getTime() == currentTime) {
              parent.classList.add('day-end');

              if (events[events.length - 1].endHalfDay || this.options.alwaysHalfDay) {
                parent.classList.add('day-half'); // Find color for other half

                var otherColor = 'transparent';

                for (var i = events.length - 2; i >= 0; i--) {
                  if (events[i].endDate.getTime() != currentTime || !events[i].endHalfDay && !this.options.alwaysHalfDay) {
                    otherColor = events[i].color;
                    break;
                  }
                }

                parent.style.background = "linear-gradient(135deg, ".concat(events[events.length - 1].color, ", ").concat(events[events.length - 1].color, " 49%, ").concat(otherColor, " 51%, ").concat(otherColor, ")");
              } else if (this.options.roundRangeLimits) {
                parent.classList.add('round-right');
              }
            }

            break;

          case 'custom':
            if (this.options.customDataSourceRenderer) {
              this.options.customDataSourceRenderer.call(this, elt, currentDate, events);
            }

            break;
        }
      }
    },    
    {
        key: "_zofarApplyEvents",
        value: function _zofarApplyEvents() {
          var _this2 = this;
          
          var focusEpisodes = [];
          const parsed = this.options.focusData.trim().split(',');
          for (let i=0; i<parsed.length; i++) {
        	  const xx = parseInt(parsed[i].trim());
        	  if(isNaN(xx))continue;
        	  focusEpisodes.push(xx);
          }
          var hasFocus = false;
          if (typeof focusEpisodes != "undefined" && focusEpisodes != null && focusEpisodes.length != null && focusEpisodes.length > 0) {
        	    hasFocus = true;
          }

          var zofarmonths = this.element.querySelectorAll('.zofar-months-container');
          var monthStart = null;
          var monthEnd = null;
          zofarmonths.forEach(function (month) {
      	    month.addEventListener('contextmenu', function (e) {
  	  	    	switch (e.which) {
  			        case 1:
  			            break;
  			        case 2:
  			            break;
  			        case 3:
  		    	    	e.preventDefault();
  		    	    	e.stopPropagation();
  			        	var zofarMonth = e.target.closest('.zofar-month');
  			        	if($( zofarMonth ).hasClass( "zofar-month-disabled" )){
  			        		//console.log('disabled month',zofarMonth);
  			        	}
  			        	else{
  	  			        	var date = _this2._getZofarDate(zofarMonth);
  	  			        	
  	  		                if (_this2.options.enableContextMenu) {	                  
  	  		                  if (_this2.options.contextMenuItems.length > 0) {
  	  		                    _this2._zofarOpenContextMenu(zofarMonth);
  	  		                  }
  	  		                }
  	  		                _this2._zofarTriggerEvent('monthContextMenu', {
  	  		                  element: zofarMonth,
  	  		                  date: date,
  	  		                  events: _this2.getZofarEvents(date)
  	  		                });
  			        	}
  			            break;
  			        default:
  			    }               
              });
          	
          	
  	        if (_this2.options.enableRangeSelection) {
  	        	if(!hasFocus)
  	    	    month.addEventListener('mousedown', function (e) {
  	    	    	e.stopPropagation();
  	    	    	var zofarMonth = e.target.closest('.zofar-month');
			        if($( zofarMonth ).hasClass( "zofar-month-disabled" )){
  			        	//console.log('disabled month',zofarMonth);
  			        }
  			        else{
		  	    	   	switch (e.which) {
		  		            case 1:
		  			       	    monthStart = _this2._getZofarStartDate(zofarMonth);
		  			       	    monthEnd = _this2._getZofarEndDate(zofarMonth); 
		  		                break;
		  		            case 2:
		  		                break;
		  		            case 3:		                
		  		                break;
		  		            default:
		  		        } 
  			        }
  	    	    });
  	        	if(!hasFocus)
  	    	    month.addEventListener('mouseup', function (e) {
  	        	    e.stopPropagation();
  	    	    	var zofarMonth = e.target.closest('.zofar-month');
			        if($( zofarMonth ).hasClass( "zofar-month-disabled" )){
  			        	//console.log('disabled month',zofarMonth);
  			        }
  			        else{
	  		    	    switch (e.which) {
	  		    	        case 1:
	  			        	    monthEnd = _this2._getZofarEndDate(zofarMonth);  
	  			        	    monthEnd = new Date(monthEnd.getFullYear(), monthEnd.getMonth() + 1, 0); 
	  			        	    
	  			        	    if(monthEnd < monthStart){
	  			        	    	var monthTmp = monthEnd;
	  			        	    	monthEnd = monthStart;
	  			        	    	monthStart = monthTmp;
	  			        	    	monthStart = new Date(monthStart.getFullYear(), monthStart.getMonth() , 1);
	  			        	    	monthEnd = new Date(monthEnd.getFullYear(), monthEnd.getMonth() + 1 , 0);
	  			        	    }
	  		        	    
	  			                _this2._triggerEvent('selectRange', {
	  			                    startDate: monthStart,
	  			                    endDate: monthEnd,
	  			                    events: _this2.getEventsOnRange(monthStart, new Date(monthEnd.getFullYear(), monthEnd.getMonth(), monthEnd.getDate() + 1))
	  			                  });
	  			        	    
	  			        	    monthStart = monthEnd = null;
	  		    	            break;
	  		    	        case 2:
	  		    	            break;
	  		    	        case 3:
	  		    	            break;
	  		    	        default:
	  		    	    }
  			        }
  	    	    });
  	        }
          });

          if (this.options.enableRangeSelection) {
            // Release range selection
        	if(!hasFocus)
            window.addEventListener('mouseup', function (e) {
              if (_this2._mouseDown) {
                _this2._mouseDown = false;

                _this2._refreshRange();

                var minDate = _this2._rangeStart < _this2._rangeEnd ? _this2._rangeStart : _this2._rangeEnd;
                var maxDate = _this2._rangeEnd > _this2._rangeStart ? _this2._rangeEnd : _this2._rangeStart;

                _this2._triggerEvent('selectRange', {
                  startDate: minDate,
                  endDate: maxDate,
                  events: _this2.getEventsOnRange(minDate, new Date(maxDate.getFullYear(), maxDate.getMonth(), maxDate.getDate() + 1))
                });
              }
            });
          }
          /* Responsive management */


          if (this._responsiveInterval) {
            clearInterval(this._responsiveInterval);
            this._responsiveInterval = null;
          }

          this._responsiveInterval = setInterval(function () {
            if (_this2.element.querySelector('.month') == null) {
              return;
            }

            var calendarSize = _this2.element.offsetWidth;
            var monthSize = _this2.element.querySelector('.month').offsetWidth + 10;
            _this2._nbCols = null;

            if (monthSize * 6 < calendarSize) {
              _this2._nbCols = 2;
            } else if (monthSize * 4 < calendarSize) {
              _this2._nbCols = 3;
            } else if (monthSize * 3 < calendarSize) {
              _this2._nbCols = 4;
            } else if (monthSize * 2 < calendarSize) {
              _this2._nbCols = 6;
            } else {
              _this2._nbCols = 12;
            }

            _this2.element.querySelectorAll('.month-container').forEach(function (month) {
              if (!month.classList.contains("month-".concat(_this2._nbCols))) {
                ['month-2', 'month-3', 'month-4', 'month-6', 'month-12'].forEach(function (className) {
                  month.classList.remove(className);
                });
                month.classList.add("month-".concat(_this2._nbCols));
              }
            });
          }, 300);
        }
      },
      {
      key: "_applyEvents",
      value: function _applyEvents() {
        var _this2 = this;
        
        if (this.options.displayHeader) {
          /* Header buttons */
          this.element.querySelectorAll('.year-neighbor, .year-neighbor2').forEach(function (element) {
            element.addEventListener('click', function (e) {
              if (!e.currentTarget.classList.contains('disabled')) {
                _this2.setYear(parseInt(e.currentTarget.textContent));
              }
            });
          });
          this.element.querySelector('.calendar-header .prev').addEventListener('click', function (e) {
            if (!e.currentTarget.classList.contains('disabled')) {
              var months = _this2.element.querySelector('.months-container');

              months.style.transition = 'margin-left 0.1s';
              months.style.marginLeft = '100%';
              setTimeout(function () {
                months.style.visibility = 'hidden';
                months.style.transition = '';
                months.style.marginLeft = '0';
                setTimeout(function () {
                  _this2.setYear(_this2.options.startYear - 1);
                }, 50);
              }, 100);
            }
          });
          this.element.querySelector('.calendar-header .next').addEventListener('click', function (e) {
            if (!e.currentTarget.classList.contains('disabled')) {
              var months = _this2.element.querySelector('.months-container');

              months.style.transition = 'margin-left 0.1s';
              months.style.marginLeft = '-100%';
              setTimeout(function () {
                months.style.visibility = 'hidden';
                months.style.transition = '';
                months.style.marginLeft = '0';
                setTimeout(function () {
                  _this2.setYear(_this2.options.startYear + 1);
                }, 50);
              }, 100);
            }
          });
        }

        var cells = this.element.querySelectorAll('.day:not(.old):not(.new):not(.disabled)');
        cells.forEach(function (cell) {
          /* Click on date */
          cell.addEventListener('click', function (e) {
            e.stopPropagation();

            var date = _this2._getDate(e.currentTarget);

            _this2._triggerEvent('clickDay', {
              element: e.currentTarget,
              date: date,
              events: _this2.getEvents(date)
            });
          });
          /* Click right on date */

          cell.addEventListener('contextmenu', function (e) {
            if (_this2.options.enableContextMenu) {
              e.preventDefault();

              if (_this2.options.contextMenuItems.length > 0) {
                _this2._openContextMenu(e.currentTarget);
              }
            }

            var date = _this2._getDate(e.currentTarget);

            _this2._triggerEvent('dayContextMenu', {
              element: e.currentTarget,
              date: date,
              events: _this2.getEvents(date)
            });
          });
          /* Range selection */

          if (_this2.options.enableRangeSelection) {
            cell.addEventListener('mousedown', function (e) {
              if (e.which == 1) {
                var currentDate = _this2._getDate(e.currentTarget);

                if (_this2.options.allowOverlap || _this2.isThereFreeSlot(currentDate)) {
                  _this2._mouseDown = true;
                  _this2._rangeStart = _this2._rangeEnd = currentDate;

                  _this2._refreshRange();
                }
              }
            });
            cell.addEventListener('mouseenter', function (e) {
              if (_this2._mouseDown) {
                var currentDate = _this2._getDate(e.currentTarget);

                if (!_this2.options.allowOverlap) {
                  var newDate = new Date(_this2._rangeStart.getTime());

                  if (newDate < currentDate) {
                    var nextDate = new Date(newDate.getFullYear(), newDate.getMonth(), newDate.getDate() + 1);

                    while (newDate < currentDate) {
                      if (!_this2.isThereFreeSlot(nextDate, false)) {
                        break;
                      }

                      newDate.setDate(newDate.getDate() + 1);
                      nextDate.setDate(nextDate.getDate() + 1);
                    }
                  } else {
                    var nextDate = new Date(newDate.getFullYear(), newDate.getMonth(), newDate.getDate() - 1);

                    while (newDate > currentDate) {
                      if (!_this2.isThereFreeSlot(nextDate, true)) {
                        break;
                      }

                      newDate.setDate(newDate.getDate() - 1);
                      nextDate.setDate(nextDate.getDate() - 1);
                    }
                  }

                  currentDate = newDate;
                }

                var oldValue = _this2._rangeEnd;
                _this2._rangeEnd = currentDate;

                if (oldValue.getTime() != _this2._rangeEnd.getTime()) {
                  _this2._refreshRange();
                }
              }
            });
          }
          /* Hover date */


          cell.addEventListener('mouseenter', function (e) {
            if (!_this2._mouseDown) {
              var date = _this2._getDate(e.currentTarget);

              _this2._triggerEvent('mouseOnDay', {
                element: e.currentTarget,
                date: date,
                events: _this2.getEvents(date)
              });
            }
          });
          cell.addEventListener('mouseleave', function (e) {
            var date = _this2._getDate(e.currentTarget);

            _this2._triggerEvent('mouseOutDay', {
              element: e.currentTarget,
              date: date,
              events: _this2.getEvents(date)
            });
          });
        });

        if (this.options.enableRangeSelection) {
          // Release range selection
          window.addEventListener('mouseup', function (e) {
            if (_this2._mouseDown) {
              _this2._mouseDown = false;

              _this2._refreshRange();

              var minDate = _this2._rangeStart < _this2._rangeEnd ? _this2._rangeStart : _this2._rangeEnd;
              var maxDate = _this2._rangeEnd > _this2._rangeStart ? _this2._rangeEnd : _this2._rangeStart;

              _this2._triggerEvent('selectRange', {
                startDate: minDate,
                endDate: maxDate,
                events: _this2.getEventsOnRange(minDate, new Date(maxDate.getFullYear(), maxDate.getMonth(), maxDate.getDate() + 1))
              });
            }
          });
        }
        /* Responsive management */


        if (this._responsiveInterval) {
          clearInterval(this._responsiveInterval);
          this._responsiveInterval = null;
        }

        this._responsiveInterval = setInterval(function () {
          if (_this2.element.querySelector('.month') == null) {
            return;
          }

          var calendarSize = _this2.element.offsetWidth;
          var monthSize = _this2.element.querySelector('.month').offsetWidth + 10;
          _this2._nbCols = null;

          if (monthSize * 6 < calendarSize) {
            _this2._nbCols = 2;
          } else if (monthSize * 4 < calendarSize) {
            _this2._nbCols = 3;
          } else if (monthSize * 3 < calendarSize) {
            _this2._nbCols = 4;
          } else if (monthSize * 2 < calendarSize) {
            _this2._nbCols = 6;
          } else {
            _this2._nbCols = 12;
          }

          _this2.element.querySelectorAll('.month-container').forEach(function (month) {
            if (!month.classList.contains("month-".concat(_this2._nbCols))) {
              ['month-2', 'month-3', 'month-4', 'month-6', 'month-12'].forEach(function (className) {
                month.classList.remove(className);
              });
              month.classList.add("month-".concat(_this2._nbCols));
            }
          });
        }, 300);
      }
    }, {
      key: "_refreshRange",
      value: function _refreshRange() {
        var _this3 = this;

        this.element.querySelectorAll('td.day.range').forEach(function (day) {
          return day.classList.remove('range');
        });
        this.element.querySelectorAll('td.day.range-start').forEach(function (day) {
          return day.classList.remove('range-start');
        });
        this.element.querySelectorAll('td.day.range-end').forEach(function (day) {
          return day.classList.remove('range-end');
        });

        if (this._mouseDown) {
          var minDate = this._rangeStart < this._rangeEnd ? this._rangeStart : this._rangeEnd;
          var maxDate = this._rangeEnd > this._rangeStart ? this._rangeEnd : this._rangeStart;
          this.element.querySelectorAll('.month-container').forEach(function (month) {
            var monthId = parseInt(month.dataset.monthId);

            if (minDate.getMonth() <= monthId && maxDate.getMonth() >= monthId) {
              month.querySelectorAll('td.day:not(.old):not(.new)').forEach(function (day) {
                var date = _this3._getDate(day);

                if (date >= minDate && date <= maxDate) {
                  day.classList.add('range');

                  if (date.getTime() == minDate.getTime()) {
                    day.classList.add('range-start');
                  }

                  if (date.getTime() == maxDate.getTime()) {
                    day.classList.add('range-end');
                  }
                }
              });
            }
          });
        }
      }
    }, {
      key: "_getElementPosition",
      value: function _getElementPosition(element) {
        var top = 0,
            left = 0;

        do {
          top += element.offsetTop || 0;
          left += element.offsetLeft || 0;
          element = element.offsetParent;
        } while (element);

        return {
          top: top,
          left: left
        };
      }
    }, 
    {
        key: "_zofarOpenContextMenu",
        value: function _zofarOpenContextMenu(elt) {
          //console.log('_zofarOpenContextMenu');
          //console.log(elt);
          var contextMenu = document.querySelector('.calendar-context-menu');

          if (contextMenu !== null) {
            contextMenu.style.display = 'none'; // Clear the context menu (faster method)

            while (contextMenu.firstChild) {
              contextMenu.removeChild(contextMenu.firstChild);
            }
          } else {
            contextMenu = document.createElement('div');
            contextMenu.classList.add('calendar-context-menu');
            document.body.appendChild(contextMenu);
          }

          var date = this._getZofarDate(elt);
          //console.log("date : "+date);
          var events = this.getZofarEvents(date);
          //console.log(events);
          
          var focusEpisodes = [];
          const parsed = this.options.focusData.trim().split(',');
          for (let i=0; i<parsed.length; i++) {
        	  const xx = parseInt(parsed[i].trim());
        	  if(isNaN(xx))continue;
        	  focusEpisodes.push(xx);
          }
          var hasFocus = false;
          if (typeof focusEpisodes != "undefined" && focusEpisodes != null && focusEpisodes.length != null && focusEpisodes.length > 0) {
        	    hasFocus = true;
          }
          
          for (var i = 0; i < events.length; i++) {
        	  
        	if(hasFocus && !focusEpisodes.includes(events[i].id))continue;
        	  
            var eventItem = document.createElement('div');
            eventItem.classList.add('item');
            eventItem.style.borderLeft = "4px solid ".concat(events[i].color);
            var eventItemContent = document.createElement('div');
            eventItemContent.classList.add('content');
            var text = document.createElement('span');
            text.classList.add('text');
            text.textContent = events[i].name;
            eventItemContent.appendChild(text);
            var icon = document.createElement('span');
            icon.classList.add('arrow');
            icon.innerHTML = "&rsaquo;";
            eventItemContent.appendChild(icon);
            eventItem.appendChild(eventItemContent);

            this._renderContextMenuItems(eventItem, this.options.contextMenuItems, events[i]);

            contextMenu.appendChild(eventItem);
          }

          if (contextMenu.children.length > 0) {
            var position = this._getElementPosition(elt);

            contextMenu.style.left = position.left + 25 + 'px';
            contextMenu.style.top = position.top + 25 + 'px';
            contextMenu.style.display = 'block';
            window.addEventListener('click', function (e) {
              if (!contextMenu.contains(e.target)) {
                contextMenu.style.display = 'none';
              }
            }, {
              once: true
            });
          }
        }
      },
    {
      key: "_openContextMenu",
      value: function _openContextMenu(elt) {
        var _this4 = this;

        var contextMenu = document.querySelector('.calendar-context-menu');

        if (contextMenu !== null) {
          contextMenu.style.display = 'none'; // Clear the context menu (faster method)

          while (contextMenu.firstChild) {
            contextMenu.removeChild(contextMenu.firstChild);
          }
        } else {
          contextMenu = document.createElement('div');
          contextMenu.classList.add('calendar-context-menu');
          document.body.appendChild(contextMenu);
        }

        var date = this._getDate(elt);

        var events = this.getEvents(date);

        for (var i = 0; i < events.length; i++) {
          var eventItem = document.createElement('div');
          eventItem.classList.add('item');
          eventItem.style.paddingLeft = '4px';
          eventItem.style.boxShadow = "inset 4px 0 0 0 ".concat(events[i].color);
          var eventItemContent = document.createElement('div');
          eventItemContent.classList.add('content');
          var text = document.createElement('span');
          text.classList.add('text');
          text.textContent = events[i].name;
          eventItemContent.appendChild(text);
          var icon = document.createElement('span');
          icon.classList.add('arrow');
          icon.innerHTML = "&rsaquo;";
          eventItemContent.appendChild(icon);
          eventItem.appendChild(eventItemContent);

          this._renderContextMenuItems(eventItem, this.options.contextMenuItems, events[i]);

          contextMenu.appendChild(eventItem);
        }

        if (contextMenu.children.length > 0) {
          var position = this._getElementPosition(elt);

          contextMenu.style.left = position.left + 25 + 'px';
          contextMenu.style.right = '';
          contextMenu.style.top = position.top + 25 + 'px';
          contextMenu.style.display = 'block';

          if (contextMenu.getBoundingClientRect().right > document.body.offsetWidth) {
            contextMenu.style.left = '';
            contextMenu.style.right = '0';
          } // Launch the position check once the whole context menu tree will be rendered


          setTimeout(function () {
            return _this4._checkContextMenuItemsPosition();
          }, 0);

          var closeContextMenu = function closeContextMenu(event) {
            if (event.type !== 'click' || !contextMenu.contains(event.target)) {
              contextMenu.style.display = 'none';
              window.removeEventListener('click', closeContextMenu);
              window.removeEventListener('resize', closeContextMenu);
              window.removeEventListener('scroll', closeContextMenu);
            }
          };

          window.addEventListener('click', closeContextMenu);
          window.addEventListener('resize', _onCalendarResize());
          window.addEventListener('scroll', closeContextMenu);
        }
      }
    },
    {
          key: "_onCalendarResize",
          value: function _onCalendarResize() {
       }
     },
    {
      key: "_renderContextMenuItems",
      value: function _renderContextMenuItems(parent, items, evt) {
        var subMenu = document.createElement('div');
        subMenu.classList.add('submenu');

        for (var i = 0; i < items.length; i++) {
          if (items[i].visible === false || typeof items[i].visible === "function" && !items[i].visible(evt)) {
            continue;
          }

          var menuItem = document.createElement('div');
          menuItem.classList.add('item');
          var menuItemContent = document.createElement('div');
          menuItemContent.classList.add('content');
          var text = document.createElement('span');
          text.classList.add('text');
          text.textContent = items[i].text;
          menuItemContent.appendChild(text);

          if (items[i].click) {
            (function (index) {
              menuItemContent.addEventListener('click', function () {
                document.querySelector('.calendar-context-menu').style.display = 'none';
                items[index].click(evt);
              });
            })(i);
          }

          menuItem.appendChild(menuItemContent);

          if (items[i].items && items[i].items.length > 0) {
            var icon = document.createElement('span');
            icon.classList.add('arrow');
            icon.innerHTML = "&rsaquo;";
            menuItemContent.appendChild(icon);

            this._renderContextMenuItems(menuItem, items[i].items, evt);
          }

          subMenu.appendChild(menuItem);
        }

        if (subMenu.children.length > 0) {
          parent.appendChild(subMenu);
        }
      }
    }, {
      key: "_checkContextMenuItemsPosition",
      value: function _checkContextMenuItemsPosition() {
        var menus = document.querySelectorAll('.calendar-context-menu .submenu');
        menus.forEach(function (menu) {
          var htmlMenu = menu;
          htmlMenu.style.display = 'block';
          htmlMenu.style.visibility = 'hidden';
        });
        menus.forEach(function (menu) {
          var htmlMenu = menu;

          if (htmlMenu.getBoundingClientRect().right > document.body.offsetWidth) {
            htmlMenu.classList.add('open-left');
          } else {
            htmlMenu.classList.remove('open-left');
          }
        });
        menus.forEach(function (menu) {
          var htmlMenu = menu;
          htmlMenu.style.display = '';
          htmlMenu.style.visibility = '';
        });
      }
    }, {
      key: "_getDate",
      value: function _getDate(elt) {
        var day = elt.querySelector('.day-content').textContent;
        var month = elt.closest('.month-container').dataset.monthId;
        var year = this.options.startYear;
        return new Date(year, month, day);
      }
    }, 
    {
        key: "_getZofarDate",
        value: function _getZofarDate(elt) {
            var month = 0;
            var year = 0;

            if (elt.closest('.zofar-month').dataset.yearId === undefined) {
                month = elt.closest('.zofar-month').dataset.id;
                year = elt.closest('.zofar-month').dataset.year;
            } else {
                month = elt.closest('.zofar-month').dataset.monthId;
                year = elt.closest('.zofar-month').dataset.yearId;
            }

            var date = new Date(year, month - 1, 1);  
//            date = new Date(date.toUTCString()+"+0100"); 
            return date;
        }
      },
      {
          key: "_getZofarStartDate",
          value: function _getZofarStartDate(elt) {
            var month = 0;
            var year = 0;
            if (elt.closest('.zofar-month').dataset.yearId === undefined){
                month = elt.closest('.zofar-month').dataset.id;
                year = elt.closest('.zofar-month').dataset.year;
            }else {
                month = elt.closest('.zofar-month').dataset.monthId;
                year = elt.closest('.zofar-month').dataset.yearId;
            }
            var date = new Date(year, month - 1, 1);
            return date;
          }
        },
        {
           key: "_getZofarEndDate",
            value: function _getZofarEndDate(elt) {
              var month = 0;
              var year = 0;
              if (elt.closest('.zofar-month').dataset.yearId === undefined) {
                  month = elt.closest('.zofar-month').dataset.id;
                  year = elt.closest('.zofar-month').dataset.year;
              } else {
                  month = elt.closest('.zofar-month').dataset.monthId;
                  year = elt.closest('.zofar-month').dataset.yearId;
              }

              var date = new Date(year, month , 0);
//              date = new Date(date.toUTCString()+"+0100"); 
              return date;
            }
          },
          
          {
              key: "_zofarTriggerEvent",
              value: function _zofarTriggerEvent(eventName, parameters) {
            	  //console.log('_zofarTriggerEvent '+eventName+' ('+parameters+')');
                var event = null;

                if (typeof Event === "function") {
                  event = new Event(eventName);
                } else {
                  event = document.createEvent('Event');
                  event.initEvent(eventName, false, false);
                }

                event.calendar = this;

                for (var i in parameters) {
                  event[i] = parameters[i];
                }
                //console.log(event);
                this.element.dispatchEvent(event);
                return event;
              }
            }, 
    {
      key: "_triggerEvent",
      value: function _triggerEvent(eventName, parameters) {
    	  
        var event = null;

        if (typeof Event === "function") {
          event = new Event(eventName);
        } else {
          event = document.createEvent('Event');
          event.initEvent(eventName, false, false);
        }

        event.calendar = this;

        for (var i in parameters) {
          event[i] = parameters[i];
        }
        this.element.dispatchEvent(event);
        return event;
      }
    }, {
      key: "_isDisabled",
      value: function _isDisabled(date) {
        if (this.options.minDate != null && date < this.options.minDate || this.options.maxDate != null && date > this.options.maxDate) {
          return true;
        }

        if (this.options.disabledWeekDays.length > 0) {
          for (var d = 0; d < this.options.disabledWeekDays.length; d++) {
            if (date.getDay() == this.options.disabledWeekDays[d]) {
              return true;
            }
          }
        }

        if (this.options.disabledDays.length > 0) {
          for (var d = 0; d < this.options.disabledDays.length; d++) {
            if (date.getTime() == this.options.disabledDays[d].getTime()) {
              return true;
            }
          }
        }

        return false;
      }
    }, {
      key: "_isHidden",
      value: function _isHidden(day) {
        if (this.options.hiddenWeekDays.length > 0) {
          for (var d = 0; d < this.options.hiddenWeekDays.length; d++) {
            if (day == this.options.hiddenWeekDays[d]) {
              return true;
            }
          }
        }

        return false;
      }
      /**
          * Gets the week number for a specified date.
          *
          * @param date The specified date.
          */

    }, {
      key: "getWeekNumber",
      value: function getWeekNumber(date) {
        // Algorithm from https://weeknumber.net/how-to/javascript
        var workingDate = new Date(date.getTime());
        workingDate.setHours(0, 0, 0, 0); // Thursday in current week decides the year.

        workingDate.setDate(workingDate.getDate() + 3 - (workingDate.getDay() + 6) % 7); // January 4 is always in week 1.

        var week1 = new Date(workingDate.getFullYear(), 0, 4); // Adjust to Thursday in week 1 and count number of weeks from date to week1.

        return 1 + Math.round(((workingDate.getTime() - week1.getTime()) / 86400000 - 3 + (week1.getDay() + 6) % 7) / 7);
      }
      /**
          * Gets the data source elements for a specified day.
          *
          * @param date The specified day.
          */

    }, 
    {
        key: "getZofarEvents",
        value: function getZofarEvents(date) {
          
          var startDate = new Date(date.getFullYear(), date.getMonth(), 1);
		  //startDate = new Date(startDate.getFullYear()+'-'+(startDate.getMonth()+1)+'-'+startDate.getDate()+'T0'+(((startDate.getTimezoneOffset()/60)*(-1))+1)+':00:00Z');
		  startDate = this._adjustDate(startDate);
		  //console.log('getZofarEvents startDate1 : '+startDate.toISOString());
		  
          var endDate = new Date(date.getFullYear(), date.getMonth()+1, 0);
          //endDate = new Date(endDate.getFullYear()+'-'+(endDate.getMonth()+1)+'-'+endDate.getDate()+'T0'+(((endDate.getTimezoneOffset()/60)*(-1))+1)+':00:00Z');
          endDate = this._adjustDate(endDate);
          //console.log('getZofarEvents endDate1 : '+endDate.toISOString());
          return this.getEventsOnRange(startDate, endDate);
        }
    },
    {
      key: "getEvents",
      value: function getEvents(date) {
        return this.getEventsOnRange(date, new Date(date.getFullYear(), date.getMonth(), date.getDate() + 1));
      }
      /**
          * Gets the data source elements for a specified range of days.
          *
          * @param startDate The beginning of the day range (inclusive).
       * @param endDate The end of the day range (non inclusive).
          */

    }, {
      key: "getEventsOnRange",
      value: function getEventsOnRange(startDate, endDate) {
        var events = [];

        if (this._dataSource && startDate && endDate) {
          for (var i = 0; i < this._dataSource.length; i++) {
            if (this._dataSource[i].startDate < endDate && this._dataSource[i].endDate >= startDate) {
              events.push(this._dataSource[i]);
            }
          }
        }

        return events;
      }
      /**
          * Check if there is no event on the first part, last part or on the whole specified day.
          *
          * @param date The specified day.
          * @param after Whether to check for a free slot on the first part (if `false`) or the last part (if `true`) of the day. If `null`, this will check on the whole day.
       * 
       * Usefull only if using the `alwaysHalfDay` option of the calendar, or the `startHalfDay` or `endHalfDay` option of the datasource.
          */

    }, {
      key: "isThereFreeSlot",
      value: function isThereFreeSlot(date) {
        var _this5 = this;

        var after = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : null;
        var events = this.getEvents(date);

        if (after === true) {
          return !events.some(function (evt) {
            return !_this5.options.alwaysHalfDay && !evt.endHalfDay || evt.endDate > date;
          });
        } else if (after === false) {
          return !events.some(function (evt) {
            return !_this5.options.alwaysHalfDay && !evt.startHalfDay || evt.startDate < date;
          });
        } else {
          return this.isThereFreeSlot(date, false) || this.isThereFreeSlot(date, true);
        }
      }
      /**
          * Gets the year displayed on the calendar.
          */

    }, {
      key: "getYear",
      value: function getYear() {
        return this.options.startYear;
      }
      /**
          * Sets the year displayed on the calendar.
          *
          * @param year The year to displayed on the calendar.
          */

    }, {
      key: "setYear",
      value: function setYear(year) {
    	//  console.log("setYear");
        var _this6 = this;

        var parsedYear = parseInt(year);

        if (!isNaN(parsedYear)) {
          this.options.startYear = parsedYear; // Clear the calendar (faster method)

          while (this.element.firstChild) {
            this.element.removeChild(this.element.firstChild);
          }

          if (this.options.displayHeader) {
            this._renderHeader();
          }

          var eventResult = this._triggerEvent('yearChanged', {
            currentYear: this.options.startYear,
            preventRendering: false
          });

          if (typeof this.options.dataSource === "function") {
            this.render(true);

            this._fetchDataSource(function (dataSource) {
              _this6._dataSource = dataSource;

              _this6._zofarTypedInitializeDatasourceColors();

              _this6.render(false);
            });
          } else {
            if (!eventResult.preventRendering) {
              this.render();
            }
          }
        }
      }
      /**
          * Gets the minimum date of the calendar.
          */

    }, {
      key: "getMinDate",
      value: function getMinDate() {
        return this.options.minDate;
      }
      /**
          * Sets the minimum date of the calendar.
       * 
       * This method causes a refresh of the calendar.
          *
          * @param minDate The minimum date to set.
       * @param preventRedering Indicates whether the rendering should be prevented after the property update.
          */

    }, {
      key: "setMinDate",
      value: function setMinDate(date) {
    	//  console.log("setMinDate");
        var preventRendering = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : false;

        if (date instanceof Date || date === null) {
          this.options.minDate = date;

          if (!preventRendering) {
            this.render();
          }
        }
      }
      /**
          * Gets the maximum date of the calendar.
          */

    }, {
      key: "getMaxDate",
      value: function getMaxDate() {
        return this.options.maxDate;
      }
      /**
          * Sets the maximum date of the calendar. 
       * 
       * This method causes a refresh of the calendar.
          *
          * @param maxDate The maximum date to set.
       * @param preventRedering Indicates whether the rendering should be prevented after the property update.
          */

    }, {
      key: "setMaxDate",
      value: function setMaxDate(date) {
    	//  console.log("setMaxDate");
        var preventRendering = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : false;

        if (date instanceof Date || date === null) {
          this.options.maxDate = date;

          if (!preventRendering) {
            this.render();
          }
        }
      }
      /**
          * Gets the current style used for displaying data source.
          */

    }, {
      key: "getStyle",
      value: function getStyle() {
        return this.options.style;
      }
      /**
          * Sets the style to use for displaying data source. 
       * 
       * This method causes a refresh of the calendar.
          *
          * @param style The style to use for displaying data source ("background", "border" or "custom").
       * @param preventRedering Indicates whether the rendering should be prevented after the property update.
          */

    }, {
      key: "setStyle",
      value: function setStyle(style) {
    	 // console.log("setStyle");
        var preventRendering = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : false;
        this.options.style = style == 'background' || style == 'border' || style == 'custom' ? style : 'border';

        if (!preventRendering) {
          this.render();
        }
      }
      /**
          * Gets a value indicating whether the user can select a range which overlapping an other element present in the datasource.
          */

    }, {
      key: "getAllowOverlap",
      value: function getAllowOverlap() {
        return this.options.allowOverlap;
      }
      /**
          * Sets a value indicating whether the user can select a range which overlapping an other element present in the datasource.
          *
          * @param allowOverlap Indicates whether the user can select a range which overlapping an other element present in the datasource.
          */

    }, {
      key: "setAllowOverlap",
      value: function setAllowOverlap(allowOverlap) {
        this.options.allowOverlap = allowOverlap;
      }
      /**
          * Gets a value indicating whether the weeks number are displayed.
          */

    }, {
      key: "getDisplayWeekNumber",
      value: function getDisplayWeekNumber() {
        return this.options.displayWeekNumber;
      }
      /**
          * Sets a value indicating whether the weeks number are displayed.
       * 
       * This method causes a refresh of the calendar.
          *
          * @param  displayWeekNumber Indicates whether the weeks number are displayed.
       * @param preventRedering Indicates whether the rendering should be prevented after the property update.
          */

    }, {
      key: "setDisplayWeekNumber",
      value: function setDisplayWeekNumber(displayWeekNumber) {
    	//  console.log("setDisplayWeekNumber");
        var preventRendering = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : false;
        this.options.displayWeekNumber = displayWeekNumber;

        if (!preventRendering) {
          this.render();
        }
      }
      /**
          * Gets a value indicating whether the calendar header is displayed.
          */

    }, {
      key: "getDisplayHeader",
      value: function getDisplayHeader() {
        return this.options.displayHeader;
      }
      /**
          * Sets a value indicating whether the calendar header is displayed.
       * 
       * This method causes a refresh of the calendar.
          *
          * @param displayHeader Indicates whether the calendar header is displayed.
       * @param preventRedering Indicates whether the rendering should be prevented after the property update.
          */

    }, {
      key: "setDisplayHeader",
      value: function setDisplayHeader(displayHeader) {
    	//  console.log("setDisplayHeader");
        var preventRendering = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : false;
        this.options.displayHeader = displayHeader;

        if (!preventRendering) {
          this.render();
        }
      }
      /**
          * Gets a value indicating whether the data source must be rendered on disabled days.
          */

    }, {
      key: "getDisplayDisabledDataSource",
      value: function getDisplayDisabledDataSource() {
        return this.options.displayDisabledDataSource;
      }
    }, {
      key: "setDisplayDisabledDataSource",
      value: function setDisplayDisabledDataSource(displayDisabledDataSource) {
    	 // console.log("setDisplayDisabledDataSource");
        var preventRendering = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : false;
        this.options.displayDisabledDataSource = displayDisabledDataSource;

        if (!preventRendering) {
          this.render();
        }
      }
    }, {
      key: "getAlwaysHalfDay",
      value: function getAlwaysHalfDay() {
        return this.options.alwaysHalfDay;
      }
    }, {
      key: "setAlwaysHalfDay",
      value: function setAlwaysHalfDay(alwaysHalfDay) {
    	//  console.log("setAlwaysHalfDay");
        var preventRendering = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : false;
        this.options.alwaysHalfDay = alwaysHalfDay;

        if (!preventRendering) {
          this.render();
        }
      }
    }, {
      key: "getEnableRangeSelection",
      value: function getEnableRangeSelection() {
        return this.options.enableRangeSelection;
      }
    }, {
      key: "setEnableRangeSelection",
      value: function setEnableRangeSelection(enableRangeSelection) {
    	 // console.log("setEnableRangeSelection");
        var preventRendering = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : false;
        this.options.enableRangeSelection = enableRangeSelection;

        if (!preventRendering) {
          this.render();
        }
      }
    }, {
      key: "getDisabledDays",
      value: function getDisabledDays() {
        return this.options.disabledDays;
      }
    }, {
      key: "setDisabledDays",
      value: function setDisabledDays(disabledDays) {
    	 // console.log("setDisabledDays");
        var preventRendering = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : false;
        this.options.disabledDays = disabledDays instanceof Array ? disabledDays : [];

        if (!preventRendering) {
          this.render();
        }
      }
    }, {
      key: "getDisabledWeekDays",
      value: function getDisabledWeekDays() {
        return this.options.disabledWeekDays;
      }
    }, {
      key: "setDisabledWeekDays",
      value: function setDisabledWeekDays(disabledWeekDays) {
        var preventRendering = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : false;
        this.options.disabledWeekDays = disabledWeekDays instanceof Array ? disabledWeekDays : [];

        if (!preventRendering) {
          this.render();
        }
      }
    }, {
      key: "getHiddenWeekDays",
      value: function getHiddenWeekDays() {
        return this.options.hiddenWeekDays;
      }
    }, {
      key: "setHiddenWeekDays",
      value: function setHiddenWeekDays(hiddenWeekDays) {
        var preventRendering = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : false;
        this.options.hiddenWeekDays = hiddenWeekDays instanceof Array ? hiddenWeekDays : [];

        if (!preventRendering) {
          this.render();
        }
      }
    }, {
      key: "getRoundRangeLimits",
      value: function getRoundRangeLimits() {
        return this.options.roundRangeLimits;
      }
    }, {
      key: "setRoundRangeLimits",
      value: function setRoundRangeLimits(roundRangeLimits) {
    	 // console.log("setRoundRangeLimits");
        var preventRendering = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : false;
        this.options.roundRangeLimits = roundRangeLimits;

        if (!preventRendering) {
          this.render();
        }
      }
    }, {
      key: "getEnableContextMenu",
      value: function getEnableContextMenu() {
        return this.options.enableContextMenu;
      }
    }, {
      key: "setEnableContextMenu",
      value: function setEnableContextMenu(enableContextMenu) {
    	//  console.log("setEnableContextMenu");
        var preventRendering = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : false;
        this.options.enableContextMenu = enableContextMenu;

        if (!preventRendering) {
          this.render();
        }
      }
    }, {
      key: "getContextMenuItems",
      value: function getContextMenuItems() {
        return this.options.contextMenuItems;
      }
    }, {
      key: "setContextMenuItems",
      value: function setContextMenuItems(contextMenuItems) {
        var preventRendering = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : false;
        this.options.contextMenuItems = contextMenuItems instanceof Array ? contextMenuItems : [];

        if (!preventRendering) {
          this.render();
        }
      }
    }, {
      key: "getCustomDayRenderer",
      value: function getCustomDayRenderer() {
        return this.options.customDayRenderer;
      }
    }, {
      key: "setCustomDayRenderer",
      value: function setCustomDayRenderer(customDayRenderer) {
        var preventRendering = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : false;
        this.options.customDayRenderer = typeof customDayRenderer === "function" ? customDayRenderer : null;

        if (!preventRendering) {
          this.render();
        }
      }
    }, {
      key: "getCustomDataSourceRenderer",
      value: function getCustomDataSourceRenderer() {
        return this.options.customDataSourceRenderer;
      }
    }, {
      key: "setCustomDataSourceRenderer",
      value: function setCustomDataSourceRenderer(customDataSourceRenderer) {
        var preventRendering = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : false;
        this.options.customDataSourceRenderer = typeof customDataSourceRenderer === "function" ? customDataSourceRenderer : null;

        if (!preventRendering) {
          this.render();
        }
      }
    }, {
      key: "getLanguage",
      value: function getLanguage() {
        return this.options.language;
      }
    }, {
      key: "setLanguage",
      value: function setLanguage(language) {
    	//  console.log("setLanguage");
        var preventRendering = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : false;

        if (language != null && Calendar.locales[language] != null) {
          this.options.language = language;

          if (!preventRendering) {
            this.render();
          }
        }
      }
    }, {
      key: "getDataSource",
      value: function getDataSource() {
        return this.options.dataSource;
      }
    }, {
      key: "setDataSource",
      value: function setDataSource(dataSource) {
        var _this7 = this;

        var preventRendering = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : false;
        this.options.dataSource = dataSource instanceof Array || typeof dataSource === "function" ? dataSource : [];

        if (typeof this.options.dataSource === "function") {
          this.render(true);

          this._fetchDataSource(function (dataSource) {
            _this7._dataSource = dataSource;

            _this7._zofarTypedInitializeDatasourceColors();

            _this7.render(false);
          });
        } else {
          this._dataSource = this.options.dataSource;

          this._zofarTypedInitializeDatasourceColors();

          if (!preventRendering) {
            this.render();
          }
        }
      }
    }, {
      key: "getWeekStart",
      value: function getWeekStart() {
        return this.options.weekStart ? this.options.weekStart : Calendar.locales[this.options.language].weekStart;
      }
    }, {
      key: "setWeekStart",
      value: function setWeekStart(weekStart) {
    	//  console.log("setWeekStart : "+weekStart);
        var preventRendering = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : false;
        this.options.weekStart = !isNaN(parseInt(weekStart)) ? parseInt(weekStart) : null;

        if (!preventRendering) {
          this.render();
        }
      }
    }, {
      key: "getLoadingTemplate",
      value: function getLoadingTemplate() {
        return this.options.loadingTemplate;
      }
    }, {
      key: "setLoadingTemplate",
      value: function setLoadingTemplate(loadingTemplate) {
        this.options.loadingTemplate = typeof loadingTemplate === "string" || loadingTemplate instanceof HTMLElement ? loadingTemplate : null;
      }
    }, {
      key: "addEvent",
      value: function addEvent(evt) {
    	//console.log("addEvent : "+evt);
        var preventRendering = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : false;

        this._dataSource.push(evt);

        if (!preventRendering) {
          this.render();
        }
      }
    }]);

    return Calendar;
  }();

  _exports["default"] = Calendar;

  _defineProperty(Calendar, "locales", {
	    en: {
	        days: ["Sonntag", "Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag", "Samstag"],
	        daysShort: ["So", "Mon", "Di", "Mi", "Do", "Fr", "Sa"],
	        daysMin: ["So", "Mon", "Di", "Mi", "Do", "Fr", "Sa"],
	        months: ["Jan", "Feb", "Mär", "Apr", "Mai", "Jun", "Jul", "Aug", "Sept", "Okt", "Nov", "Dez"],
	        monthsShort: ["Jan", "Feb", "Mär", "Apr", "Mai", "Jun", "Jul", "Aug", "Sept", "Okt", "Nov", "Dez"],
	        weekShort: 'W',
	        weekStart: 1
	      }
  });

  _defineProperty(Calendar, "colors", Array['#2C8FC9', '#9CB703', '#F5BB00', '#FF4A32', '#B56CE2', '#45A597']);

  if ((typeof window === "undefined" ? "undefined" : _typeof(window)) === "object") {
    window.Calendar = Calendar;
    document.addEventListener("DOMContentLoaded", function () {
      document.querySelectorAll('[data-provide="calendar"]').forEach(function (element) {
        return new Calendar(element);
      });
    });
  }
});
