$(document).ready(function() {
	
	$('span[data-toggle="tooltip"]').tooltip({
	    animated: 'fade',
	    placement: 'bottom',
	    trigger: 'click'
	});
	   
	   var datesToDisabled = ["Sep 2018", "Oct-2018"];
	   $('.zo-monthpicker-question').each(function() {
		   var monthpicker = $(this);
		   //var datesToDisabled = $(monthpicker).attr("data-dates-disabled").split(',');
		   console.log("monthpicker");
		   console.log(monthpicker);
		   console.log("datesToDisable");
		   console.log(datesToDisabled);
		   $(monthpicker).attr('readonly', true);
		   $(monthpicker).datepicker({
				autoclose: true,
				clearBtn: true,
				format: "M yyyy",
		        startView: "months", 
		        minViewMode: "months",
				language: $( monthpicker ).attr( 'data-lang' ),
		        multidate: false,
		        value:"",
		        startDate: new Date($( monthpicker ).attr( 'data-startdate' )),
		        endDate: new Date($( monthpicker ).attr( 'data-enddate' )),
		        datesDisabled: datesToDisabled,
		   }).on("changeDate", function(event) {
			    	var cDate = new Date($(this).datepicker('getDate'));
			    	cDate.setDate(new Date(cDate.getFullYear(), cDate.getMonth() +1, 0).getDate());
//			    	console.log("qwe VD cdate "+cDate);
			    	$(monthpicker).attr("data-value",cDate);
			    	$(monthpicker).attr("value",cDate);
			    	$(monthpicker).attr("placeholder",$(monthpicker).attr( 'data-placeholder' ));
//		   }).on("show", function() {
//				var inputField = $('input',this);
//				console.log('monthpicker field 0 ');console.log($(inputField));
//				var placeholder = $(inputField).attr( 'data-placeholder' );
//				$(inputField).attr( 'placeholder', placeholder);
			});
		   $(monthpicker).val($(monthpicker).attr( 'data-value' ));
		   var placeholder = $(monthpicker).attr( 'data-placeholder' );
		   console.log("placeholder : "+placeholder);
		   $(monthpicker).attr("placeholder",placeholder);
	   });
	   
	   $('.zo-monthpicker-question').ready(function() {
//		   $(this).datepicker('setDate', $(this).datepicker('setDate', $(monthpicker ).attr( 'data-value' )));
		   $(this).val($(this).attr( 'data-value' ));
		   var placeholder = $(this).attr( 'data-placeholder' );
		   console.log("placeholder : "+placeholder);
		   $(this).attr("placeholder",placeholder);
	   });
	   	   
	 /*  
	$('#monthpickerStart').datepicker({
		autoclose: true,
		format: "M yyyy",
        startView: "months", 
//        toggleActive: true,
        multidate: false,
        minViewMode: "months",
        startDate: new Date($( '#monthpickerStart' ).attr( 'data-startdate' )),
        endDate: new Date($( '#monthpickerStart' ).attr( 'data-enddate' ))
	    }).on("changeDate", function(event) {
	    	//$('#monthpickerStart').attr("data-value",$(this).datepicker('getDate'));
	    	
	    	var cDate = new Date($(this).datepicker('getDate'));
	    	
//	    	var cDate = new Date(event.date.valueOf());
	    	cDate.setDate(new Date(cDate.getFullYear(), cDate.getMonth() , 1).getDate());
//	    	console.log("start cdate "+cDate);
//	    	console.log("start cdate ÄÄÄÄÄ: "+$(this).datepicker('getDate'));
	    	$(this).attr("data-value",cDate);
	    	var cmonth = (cDate.getMonth()+1);
	    	var cmonthStr = "";
	    	if(cmonth < 10)cmonthStr += "0";
	    	cmonthStr += cmonth;
	    	var cDateStr = cmonthStr+" "+cDate.getFullYear();
	    	
	    	$('#monthpickerStart').attr("data-date",cDateStr);
	    	
	    	console.log("VD EVENT: "+event);
	    	
	    	console.log("VD 1 Start: "+$("#monthpickerStart").datepicker('getDate'));
	    	
	    	console.log("VD 2 End: "+$("#monthpickerEnd").datepicker('getDate'));
	    	 
	    	
	    	$('#monthpickerEnd').datepicker('setStartDate', cDateStr);
	    	
	    	
	    	if ((typeof $("#monthpickerStart").datepicker('getDate') !== 'undefined' && moment($("#monthpickerStart").datepicker('getDate')).isValid()) 
	    			&& 
	    			(typeof $("#monthpickerEnd").datepicker('getDate') !== 'undefined' &&moment($("#monthpickerEnd").datepicker('getDate')).isValid())) 
	    	{
	    		if ($("#monthpickerStart").datepicker('getDate') >= $("#monthpickerEnd").datepicker('getDate')) {
	    			$("#monthpickerEnd").datepicker('setDate', ''+cDateStr);
	    			$('#monthpickerEnd').attr("data-date",cDateStr);
	    			
	    		}
	    	} else {
//	    		$("#monthpickerEnd").datepicker('setDate', ''+cDateStr);
	    	}
	    	
			
//	   uncomment to disable defined Month's
	    	
//	  var year = $("th.datepicker-switch").eq(1).text();  
//	  $(".month").each(function(index, element) {
//	    var el = $(element);
//	    
//	    var hideMonth = $.grep( datesToDisable, function( n, i ) {
//	                  return n.substr(4, 4) == year && n.substr(0, 3) == el.text();
//	                });
//	    if (hideMonth.length)
//	      el.addClass('disabled');
//	  });
	  
	});
*/
	/*
	$('#monthpickerEnd').datepicker({
		autoclose: true,
		format: "M yyyy",
        startView: "months", 
        minViewMode: "months",
        multidate: false,
        startDate: new Date($( '#monthpickerEnd' ).attr( 'data-startdate' )),
        endDate: new Date($( '#monthpickerEnd' ).attr( 'data-enddate' ))
	    }).on("changeDate", function(event) {
	    	//$('#monthpickerEnd').attr("data-value",$(this).datepicker('getDate'));
//	    	$('#monthpickerEnd').attr("data-date",$(this).datepicker('getDate'));
	    	var cDate = new Date($(this).datepicker('getDate'));
//	    	var cDate = new Date(event.date.valueOf());
	    	cDate.setDate(new Date(cDate.getFullYear(), cDate.getMonth() +1, 0).getDate());
	    	console.log("end cdate "+cDate);
	    	$(this).attr("data-value",cDate);
	    	var cmonth = (cDate.getMonth()+1);
	    	var cmonthStr = "";
	    	if(cmonth < 10)cmonthStr += "0";
	    	cmonthStr += cmonth;
	    	var cDateStr = cmonthStr+" "+cDate.getFullYear();
	    	
	    	$('#monthpickerEnd').attr("data-date",cDateStr);
	    	
//	    	$('#monthpickerStart').datepicker('setEndDate', cDateStr);
//			$('#monthpickerEnd').datepicker('setDate', cDateStr);

	});
	*/

//	$("#monthpickerStart").datepicker('setDate', ''+$( '#monthpickerStart' ).attr( 'data-date' ));
});


// CF 2022-02-04 helper function for overflow protection
function numberOfNewlines(some_string) {
	return some_string.split("\n").length - 1;
}

// CF 2022-02-04 helper function for overflow protection
function countFullStringLength(some_string){
	return some_string.length + numberOfNewlines(some_string);
}

$('.carousel').carousel({
	interval : false,
	keyboard : false,
	wrap : false
});


$(".zo-display-table").each(function() {

	var masterSelector = $(this);
	var carouselSelector = $(this);
	var headerSelector = $(".zo-display-table-header-row", masterSelector);
	var bodySelector = $(".zo-display-table-body", masterSelector);

	var items = $('.zo-display-table-body-item-label', bodySelector);
	$(items).each(function() {
		var labelId = $(this).attr('data-label');

		var labelSelector = $("#" + labelId, headerSelector);
		var labelContent = $(labelSelector).text();

		if (window.matchMedia('(max-width: 767px)').matches) {
			$(this).text(labelContent);
		} else
			$(this).text("");

	});

});

function sizeCovers() {
    $(".cover").each(function() {
//    console.log($(this).parent());
    var h = $(this).parent().outerHeight();
    var w = $(this).parent().outerWidth();
    $(this).css("height", h);
    $(this).css("width", w);
    
    //$(this).css("min-height", h);
    //$(this).css("min-width", w);
    
    $(this).css("max-height", h);
    $(this).css("max-width", w);
  });
};

$(document).ready(function() {
	sizeCovers();
});

$(window).resize(function() {
	sizeCovers();
});

$(document).ready(function(){
    $("#form").attr("autocomplete", "off");
});

$(".disclaimer").each(function() {
	var masterSelector = $(this);
	var containerSelector = $(".scroll-pane", this);
	var referenceId = $(masterSelector).attr('data-reference');
	var referenceSelector = $("#" + referenceId);

	$(containerSelector).on('scroll', function(e) {
		$(referenceSelector).attr('checked', true);
	});
});


$(".zofar-link").each(function() {
	var masterSelector = $(this);
	var referenceId = $(masterSelector).attr('data-reference');
	var referenceSelector = $("#" + referenceId);
	$(masterSelector).on('click', function(e) {
		$(referenceSelector).attr('checked', true);
	});
});

$(".display-calendar")
		.each(

				function() {
					var masterSelector = $(this);

					var containerSelector = $('.calendar-container',
							masterSelector);
					var carouselSelector = $(".carousel", containerSelector);
					var legendSelector = $(".calendar-legend", masterSelector);
					var indicatorSelector = $(".carousel-indicators",
							carouselSelector);
					var headerSelector = $(".calendar-header-title",
							masterSelector);
					
					var headColSelector = $(".calendar-head",
							containerSelector);

					var controlSelector = $('.calendar-control', masterSelector);

					function setIndicators() {
						var indicators = $('li', indicatorSelector);
						$(indicators).each(
								function() {
									var isActive = $(this).hasClass("active");
									var carouselId = $(this)
											.attr('data-target');
									var slideNumber = $(this).attr(
											'data-slide-to');

									var content = $(this).text();
									if (content.trim() == "")
										content = $(this).text(
												parseInt(slideNumber) + 1);

									if (!isActive)
										$(this).attr(
												'onClick',
												'$("' + carouselId
														+ '").carousel('
														+ slideNumber + ');');
									else
										$(this).removeAttr('onClick');
								});
					}

					$(carouselSelector).ready(setIndicators);
					$(carouselSelector).on('slid.bs.carousel', setIndicators);


					$(carouselSelector).on('slide.bs.carousel', function() {
						$(headerSelector).css({
							'opacity' : 0
						});
					});

					$(carouselSelector).on(
							'slid.bs.carousel',
							function() {
								var currentSlide = $(this).find('.active');
								var newHeadline = $('.row-title', currentSlide)
										.attr('data-title');

								// carouselPosition();
// $(headerSelector).text(newHeadline);
								$(headerSelector).css({
									'opacity' : 1
								});
							});

					function debug() {
						console.log("debug flip over");
						$(this).delay(1000).queue(function() {

							$(carouselSelector).trigger('slid.bs.carousel');

							$(this).dequeue();

						});

						$('.carousel-control-prev', controlSelector).unbind(
								'click', debug);
						$('.carousel-control-next', controlSelector).unbind(
								'click', debug);
					}

					function setButtons() {
						var currentSlide = $(carouselSelector).find('.active');

						var prevSlide = currentSlide.prev();
						var prevTitle = $('.row-title', prevSlide).attr(
								'data-title');

						var nextSlide = currentSlide.next();
						var nextTitle = $('.row-title', nextSlide).attr(
								'data-title');

						var prevBtn = $('.carousel-control-prev .label',
								controlSelector);
						var nextBtn = $('.carousel-control-next .label',
								controlSelector);

						if (!prevTitle) {
							var lastSlide = $('.carousel-item:last',
									carouselSelector);
							prevTitle = $('.row-title', lastSlide).attr(
									'data-title');

							$('.carousel-control-prev', controlSelector).on(
									'click', debug);

						} else
							$('.carousel-control-prev', controlSelector)
									.unbind('click', debug);

						if (!nextTitle) {
							var firstSlide = $('.carousel-item:first',
									carouselSelector);
							nextTitle = $('.row-title', firstSlide).attr(
									'data-title');
							$('.carousel-control-next', controlSelector).on(
									'click', debug);
						} else
							$('.carousel-control-next', controlSelector)
									.unbind('click', debug);

						prevBtn.text(prevTitle);
						nextBtn.text(nextTitle);

						prevBtn.parent().show();
						nextBtn.parent().show();
					}

					$(carouselSelector).ready(setButtons);
					$(carouselSelector).on('slid.bs.carousel', setButtons);
					$(carouselSelector).trigger('slid.bs.carousel');
					
					// Swipe Support
// $(carouselSelector).swiperight(function() {
// $(carouselSelector).carousel('prev');
// });
// $(carouselSelector).swipeleft(function() {
// $(carouselSelector).carousel('next');
// });
					
					$(headColSelector)
					.each(
							function() {
								var parentSelector = $(this).parent();
								var rows = $(
										'.calendar-row-container .calendar-row',
										parentSelector);
						
								$(this).insertBefore(rows);
							});
					

					
					function short(){
							$('.calendar-header-cell',$('.calendar-row-container')).each(
										function() {
											
											var title = $(this).attr('data-full-title');									
											var newTitle = title;
											if (window.matchMedia('(max-width: 767px)').matches) {
//												newTitle = title.toUpperCase();
//												newTitle = newTitle.replace("AE","");
//												newTitle = newTitle.replace("OE","");
//												newTitle = newTitle.replace("UE","");
//												
//												newTitle = newTitle.replace("Ä","");
//												newTitle = newTitle.replace("Ö","");
//												newTitle = newTitle.replace("Ü","");
//												
//												var len = newTitle.length;
//												newTitle = newTitle.substring(0,3);
//												if(len > 3) newTitle = newTitle + ".";
											}
											$(this).text(newTitle);
											$(this).attr('title',newTitle);
										}
							);
					}
					
					$('.calendar-header-cell',$('.calendar-row-container')).ready(short);
					$(window).resize(short);	


					$('.calendar-legend-item', legendSelector).each(
							function(ev) {
								var label = $(this).attr('data-label');
								var color = $(this).attr('data-color');
								var pattern = $(this).attr('data-pattern');

								$(this).attr('title', label);
								$(this).text(label);
								// $(this).css('color', color);
								// $(this).css('background-image',
								// 'url(' + pattern + ')');

							});

					$('.calendar-legend-item-icon', legendSelector).each(
							function(ev) {
								var color = $(this).attr('data-color');
								var pattern = $(this).attr('data-pattern');

								$(this).css('background-color', color);
								$(this).css('background-image',
										'url(' + pattern + ')');

							});

					$('.display-calendar-slot', containerSelector).each(
							function(ev) {
								var eventId = $(this).attr('data-event-id');
								var currentEventSelector = $(
										'.calendar-legend-item[data-event-id="'
												+ eventId + '"]',
										legendSelector);

								var label = $(currentEventSelector).attr(
										'data-label');
								var color = $(currentEventSelector).attr(
										'data-color');
								var pattern = $(currentEventSelector).attr(
										'data-pattern');

								var meta = $(this).attr('data-meta-info');

								if ((typeof meta != "undefined")
										&& (meta != null)) {
									$(this).attr('data-toggle', 'tooltip');
									$(this).attr('data-html', 'true');
									$(this).attr(
											'title',
											'<em>' + label + '</em><br/>'
													+ meta);
									$(this).tooltip();
								} else if ((typeof label != "undefined")
										&& (label != null)) {
									$(this).attr('data-toggle', 'tooltip');
									$(this).attr('data-html', 'true');
									$(this).attr(
											'title',
											'<em>' + label + '</em>');
									$(this).tooltip();
								}
								// $(this).text(label);
								$(this).css('background-color', color);
								$(this).css('background-image',
										'url(' + pattern + ')');

							});

					$('.display-calendar-empty', containerSelector).each(
							function(ev) {
								$(this).attr('title', '');
								$(this).text('empty');
								$(this).css('background-color', "inherit");
								$(this).css('background-image', 'inherit');

							});

				});

$(".calendar")
		.each(
				function() {
					var masterSelector = $(this);
					var legendSelector = $('.calendar-legend', masterSelector);
					var containerSelector = $('.calendar-container',
							masterSelector);
					var carouselSelector = $(".carousel", containerSelector);
					var indicatorSelector = $(".carousel-indicators",
							carouselSelector);
					
					

					var controlSelector = $('.calendar-control', masterSelector);
					var headerSelector = $(".calendar-header-title",
							masterSelector)
							
					var headColSelector = $(".calendar-head",
							containerSelector);

					function setIndicators() {
						var indicators = $('li', indicatorSelector);
						$(indicators).each(
								function() {
									var isActive = $(this).hasClass("active");
									var carouselId = $(this)
											.attr('data-target');
									var slideNumber = $(this).attr(
											'data-slide-to');

									var content = $(this).text();
									if (content.trim() == "")
										content = $(this).text(
												parseInt(slideNumber) + 1);

									if (!isActive)
										$(this).attr(
												'onClick',
												'$("' + carouselId
														+ '").carousel('
														+ slideNumber + ');');
									else
										$(this).removeAttr('onClick');
								});
					}
					$(carouselSelector).ready(setIndicators);
					$(carouselSelector).on('slid.bs.carousel', setIndicators);
					//$(carouselSelector).on('slid.bs.carousel', sizeCovers);


					$(headColSelector)
							.each(
									function(ev) {
										var parentSelector = $(this).parent();
										var rows = $(
												'.calendar-row-container .calendar-row',
												parentSelector);
										$(this).insertBefore(rows);
									});
					
					
					function short(){
						$('.calendar-header-cell',$('.calendar-row-container')).each(
									function() {
										
										var title = $(this).attr('data-full-title');									
										var newTitle = title;
										if (window.matchMedia('(max-width: 767px)').matches) {
//											newTitle = title.toUpperCase();
//											newTitle = newTitle.replace("AE","");
//											newTitle = newTitle.replace("OE","");
//											newTitle = newTitle.replace("UE","");
//											
//											newTitle = newTitle.replace("Ä","");
//											newTitle = newTitle.replace("Ö","");
//											newTitle = newTitle.replace("Ü","");
//											var len = newTitle.length;
//											newTitle = newTitle.substring(0,3);
//											if(len > 3) newTitle = newTitle + ".";
										}
										$(this).text(newTitle);
										$(this).attr('title',newTitle);
									}
						);
				}
				
				$('.calendar-header-cell',$('.calendar-row-container')).ready(short);
				$(window).resize(short);	

					$(carouselSelector).on('slide.bs.carousel', function() {

						$(headerSelector).css({
							'opacity' : 0
						});
					});

					$(carouselSelector).on(
							'slid.bs.carousel',
							function() {
								var currentSlide = $(this).find('.active');
								var newHeadline = $('.row-title', currentSlide)
										.attr('data-title');

								$(headerSelector).text(newHeadline);
								$(headerSelector).css({
									'opacity' : 0
								});
							});

					function debug() {
						console.log("debug flip over");
						$(this).delay(1000).queue(function() {

							$(carouselSelector).trigger('slid.bs.carousel');

							$(this).dequeue();

						});

						$('.carousel-control-prev', controlSelector).unbind(
								'click', debug);
						$('.carousel-control-next', controlSelector).unbind(
								'click', debug);
					}

					function setButtons() {
						var currentSlide = $(carouselSelector).find('.active');

						var prevSlide = currentSlide.prev();
						var prevTitle = $('.row-title', prevSlide).attr(
								'data-title');

						var nextSlide = currentSlide.next();
						var nextTitle = $('.row-title', nextSlide).attr(
								'data-title');

						var prevBtn = $('.carousel-control-prev .label',
								controlSelector);
						var nextBtn = $('.carousel-control-next .label',
								controlSelector);

						if (!prevTitle) {
							var lastSlide = $('.carousel-item:last',
									carouselSelector);
							prevTitle = $('.row-title', lastSlide).attr(
									'data-title');
							$('.carousel-control-prev', controlSelector).on(
									'click', debug);
						} else
							$('.carousel-control-prev', controlSelector)
									.unbind('click', debug);

						if (!nextTitle) {
							var firstSlide = $('.carousel-item:first',
									carouselSelector);
							nextTitle = $('.row-title', firstSlide).attr(
									'data-title');
							$('.carousel-control-next', controlSelector).on(
									'click', debug);
						} else
							$('.carousel-control-next', controlSelector)
									.unbind('click', debug);

						prevBtn.text(prevTitle);
						nextBtn.text(nextTitle);

						prevBtn.parent().show();
						nextBtn.parent().show();
					}

					$(carouselSelector).ready(setButtons);
					$(carouselSelector).on('slid.bs.carousel', setButtons);
					$(carouselSelector).trigger('slid.bs.carousel');

					$(containerSelector)
							.ready(
									function() {

										$('.calendar-cell', containerSelector)
												.each(
														function() {
															var matrix = $(this)
																	.closest(
																			'.calendar-row-container');
															var legendId = matrix
																	.attr('data-legend');

															var cell = $(this);
															$(
																	'input[type="checkbox"]',
																	cell)
																	.each(
																			function() {
																				var checkbox = $(this);
																				
// $(checkbox).bind('mousedown.block mouseup.block', function(e) {
// console.log("mouse.block 0");
// e.preventDefault();
// e.stopPropagation();
//																					
// });
																				
																				var slotId = $(
																						checkbox)
																						.attr(
																								'value');

																				var legendSelector = $(
																						'[id="'
																								+ legendId
																								+ '"] input[id="'
																								+ legendId
																								+ '_legend_'
																								+ slotId
																								+ '"]',
																						masterSelector);

																				var label = $(
																						legendSelector)
																						.attr(
																								'data-label');
																				var color = $(
																						legendSelector)
																						.attr(
																								'data-color');
																				var pattern = $(
																						legendSelector)
																						.attr(
																								'data-pattern');

// var tmp = false;
// var tmp = checkbox
// .attr('checked');
// if ((typeof tmp == "undefined")
// || (tmp == null)) {
// tmp = false;
// }
//																				console.log(checkbox);
																				var checked;
// var checked = checkbox.checked;
// if ((typeof checked == "undefined")
// || (checked == null)) {
//																					
// checked = checkbox.prop( "checked" );
// console.log("prop : "+checked);
// }
																				
																				if ((typeof checked == "undefined")
																						|| (checked == null)) {
																					
																					checked = (checkbox.attr( "checked" ) == 'checked');
//																					console.log("attr 0: "+checked);
																				}
																				
																				if ((typeof checked == "undefined")
																						|| (checked == null)) {
//																					console.log("default 0");
																					checked = false;
																				}
																				
//																				console.log("checked 0: "+checked);


																				checkbox
																						.parent()
																						.attr(
																								'title',
																								label);
																				checkbox
																						.parent()
																						.css(
																								'background-color',
																								color);
																				checkbox
																						.parent()
																						.css(
																								'background-image',
																								'url('
																										+ pattern
																										+ ')');

																				if (!checked) {
																					checkbox
																							.parent()
																							.attr(
																									'title',
																									'');
																					checkbox
																							.parent()
																							.css(
																									'background-color',
																									'inherit');
																					checkbox
																							.parent()
																							.css(
																									'background-image',
																									'inherit');
																				}
																			});
														});

									});

					var start = null;
					var stop = null;
					var inProgress = false;
					
// 'mouseup mousedown mouseleave'

					$(containerSelector)
							.on(
									'mousedown mouseup mousemove mouseleave',
									function(e) {
										var type = e.type;
										
										var targetCell = null;
										$(e.target.closest('.calendar-cell')).each(function() {
											targetCell = $(this);	
										});
										
//										if (!inProgress)
//											start = null;
//										if (inProgress)
//											stop = null;
//										
// console.log("type : "+type+" ("+targetCell+")");

										if (type == "mousedown") {
											inProgress = true;
											start = null;
											stop = null;
											
											if ((targetCell != null) &&(typeof targetCell != undefined)){
												start = targetCell;
											}
//											console.log("type : "+type+" inProgress : "+inProgress+" ("+start+")");
										}
										if ((type == "mouseup") && inProgress) {
											inProgress = false;
											if ((targetCell != null) &&(typeof targetCell != undefined)){
												stop = targetCell;
											}
//											console.log("type : "+type+" inProgress : "+inProgress+" ("+stop+")");
										}
										if ((type == "mousemove") && inProgress) {
											if ((targetCell != null) &&(typeof targetCell != undefined)){
												stop = targetCell;
//												console.log("x : "+targetCell);
											}
//											console.log("type : "+type+" inProgress : "+inProgress+" ("+stop+")");
										}
										if ((type == "mouseleave") && inProgress) {
											inProgress = false;
											if ((targetCell != null) &&(typeof targetCell != undefined)){
												stop = targetCell;
											}
//											console.log("type : "+type+" inProgress : "+inProgress+" ("+stop+")");
										}
										
										// if ((type == "click")&&(!inProgress)) {
										// inProgress = false;
										// start = targetCell;
										// stop = targetCell;
										// }
										
										if(!inProgress){
											//console.log(start);
											//console.log(stop);
										}

										if (start && stop && !inProgress) {
											var startRow = parseInt($(start)
													.closest('.calendar-row')
													.attr('data-rowindex'), 10);
											var stopRow = parseInt($(stop)
													.closest('.calendar-row')
													.attr('data-rowindex'), 10);

											if (startRow > stopRow) {
												var tmp = stop;
												stop = start;
												start = tmp;

												startRow = parseInt(
														$(start)
																.closest(
																		'.calendar-row')
																.attr(
																		'data-rowindex'),
														10);
												stopRow = parseInt(
														$(stop)
																.closest(
																		'.calendar-row')
																.attr(
																		'data-rowindex'),
														10);
											}

											var startCell = parseInt($(start)
													.closest('.calendar-cell')
													.attr('data-cellindex'), 10);
											var stopCell = parseInt($(stop)
													.closest('.calendar-cell')
													.attr('data-cellindex'), 10);
											
											console.log(startCell+" ==> "+stopCell)

											if ((startRow == stopRow)
													&& (startCell > stopCell)) {
												var tmp = stop;
												stop = start;
												start = tmp;

												startCell = parseInt(
														$(start)
																.closest(
																		'.calendar-cell')
																.attr(
																		'data-cellindex'),
														10);
												stopCell = parseInt(
														$(stop)
																.closest(
																		'.calendar-cell')
																.attr(
																		'data-cellindex'),
														10);
											}

											for (var i = startRow; i <= stopRow; i++) {
												var currentRow = $(
														'.calendar-row[data-rowindex="'
																+ i + '"]',
														containerSelector);
												var cells = $(
														'.calendar-cell[data-cellindex]',
														currentRow);
												$(cells)
														.each(
																function() {
																	var currentCell = parseInt(
																			$(
																					this)
																					.attr(
																							'data-cellindex'),
																			10);
																	
																	var cell = $(this);

																	var valid = true;
																	if (i == startRow) {
																		if (currentCell < startCell)
																			valid = false;
																	}
																	if (i == stopRow) {
																		if (currentCell > stopCell)
																			valid = false;
																	}
																	
																	var matrix = $(
																			this)
																			.closest(
																					'.calendar-row-container');
																	
																	var legendId = matrix
																	.attr('data-legend');
																	
																	var selected = $(
																			'[id="'
																					+ legendId
																					+ '"] input[name="'
																					+ legendId
																					+ '_legend"]:checked',
																			masterSelector)
																			.val();
																	
																	if ((typeof selected == "undefined")
																			|| (selected == null)){
																		valid = false;
																	}
																	
																	
																	if (valid === true) {
																		var label = $(
																				'[id="'
																						+ legendId
																						+ '"] input[name="'
																						+ legendId
																						+ '_legend"]:checked',
																				masterSelector)
																				.attr(
																						'data-label');
																		var color = $(
																				'[id="'
																						+ legendId
																						+ '"] input[name="'
																						+ legendId
																						+ '_legend"]:checked',
																				masterSelector)
																				.attr(
																						'data-color');
																		var pattern = $(
																				'[id="'
																						+ legendId
																						+ '"] input[name="'
																						+ legendId
																						+ '_legend"]:checked',
																				masterSelector)
																				.attr(
																						'data-pattern');

																		var checkbox = $(
																				'input[id="'
																						+ cell
																								.attr('data-item')
																						+ ':'
																						+ selected
																						+ '"]',
																				cell);

																		var checked;

																		
																		if ((typeof checked == "undefined")
																				|| (checked == null)) {
																			
																			checked = (checkbox.attr( "checked" ) == 'checked');
																		}
																		
																		if ((typeof checked == "undefined")
																				|| (checked == null)) {
																			checked = false;
																		}	

																		if(!checked)checkbox.attr( "checked", "checked");
																		else checkbox.removeAttr( "checked");

																		var checked2;
																		
																		if ((typeof checked2 == "undefined")
																				|| (checked2 == null)) {
																			
																			checked2 = (checkbox.attr( "checked" ) == 'checked');
																		}
																		
																		if ((typeof checked2 == "undefined")
																				|| (checked2 == null)) {
																			checked2 = false;
																		}

	
																		if (checked2) {
																			
																			checkbox
																					.parent()
																					.attr(
																							'title',
																							label);
																			checkbox
																					.parent()
																					.css(
																							'background-color',
																							color);
																			checkbox
																					.parent()
																					.css(
																							'background-image',
																							'url('
																									+ pattern
																									+ ')');
																		} else {
																			checkbox
																					.parent()
																					.attr(
																							'title',
																							'');
																			checkbox
																					.parent()
																					.css(
																							'background-color',
																							'inherit');
																			checkbox
																					.parent()
																					.css(
																							'background-image',
																							'inherit');
																		}
																		
																	}
																});
											}
											start = null;
											stop = null;
											inProgress = false;
										}
//										else {
//											start = null;
//											stop = null;
//											inProgress = false;
//										}

										if (type == "click") {
											$(targetCell)
													.each(
															function() {
																var firstElem = $(
																		this)
																		.first()
																if ((typeof firstElem == "undefined")
																		|| (firstElem == null)) {
																} else {
																	// $('html,
																	// body').animate({
																	// scrollTop:
																	// (firstElem.offset().top-10)
																	// },500);

																	// $(
																	// 'html,
																	// body')
																	// .scrollTop(
																	// firstElem
																	// .offset().top
																	// - 15);
																}
															});
										}

									});

					$(' .modal-content .modal-body label input[type="radio"]',
							legendSelector)
							.each(
									function(ev) {
										var span = $('<span />')
												.attr(
														'style',
														'margin-left:10px;display:inline-block;background-color:'
																+ $(this)
																		.attr(
																				'data-color')
																+ ';background-image:url('
																+ $(this)
																		.attr(
																				'data-pattern')
																+ ');width:10px;height:10px')
												.html('');
										$(this).after(span);

										var label = $('<span />')
												.attr('style',
														'padding-left:10px;display:inline-block;')
												.html(
														$(this).attr(
																'data-label'));
										span.after(label);
									});

					$('.calendar-legend-selected', legendSelector)
							.each(
									function(e) {
										var selectedSelector = $(this);
										var legendId = $(selectedSelector)
												.attr('data-target');

										function setSelected() {
											var selectedOption = $(
													'input[type="radio"]:checked',
													$(this));
											// console.log("selectedOption :
											// "+selectedOption.val());
											if ((typeof selectedOption == "undefined")
													|| (selectedOption == null)
													|| (typeof selectedOption
															.val() == "undefined")) {
												$(selectedSelector).attr(
														'title',
														"Aktivität auswählen");
												$(selectedSelector).text(
														"Aktivität auswählen");
												$(selectedSelector).css(
														'background-color',
														'inherit');
												$(selectedSelector).css(
														'background-image',
														'inherit');
											} else {
												var label = $(selectedOption)
														.attr('data-label');
												var color = $(selectedOption)
														.attr('data-color');
												var pattern = $(selectedOption)
														.attr('data-pattern');

											// $(selectedSelector).attr(
											// 'title', label);
											//												
											// $(selectedSelector).text(label);
											// $(selectedSelector).css(
											// 'background-color',
											// color);
											// $(selectedSelector).css(
											// 'background-image',
											// 'url(' + pattern + ')');
												
												var span = $('<span />')
												.attr(
														'style',
														'margin-left:10px;display:inline-block;background-color:'
																+ color
																+ ';background-image:url('
																+ pattern
																+ ');width:10px;height:10px')
												.html('');
												$(selectedSelector).html(span);

												var label = $('<span />')
														.attr('style',
																'padding-left:10px;display:inline-block;')
														.html(label);
												span.after(label);

											}
										}

										$(legendId, legendSelector).ready(
												setSelected);

										$(legendId, legendSelector).on(
												'hidden.bs.modal', setSelected);
									});
				});

/* bugfix wrong Carousel Heading after flip over */
$('.carousel')
		.each(
				function() {
					var ignore = false;

					if ($(this).parents('.calendar').length > 0)
						ignore = true;
					if ($(this).parents('.display-calendar').length > 0)
						ignore = true;
					if ($(this).parents('.comparison').length > 0)
						ignore = true;

					if (!ignore) {
						//
						var carouselSelector = $(this);
						var indicatorSelector = $(".carousel-indicators",
								carouselSelector);
						var headerSelector = $(
								'.text-head[data-carousel-head-id="'
										+ carouselSelector.attr('id') + '"]',
								document);
						var controlSelector = $('.carousel-control-outer',
								carouselSelector);
//						console.log(headerSelector);

						function setIndicators() {
							var indicators = $('li', indicatorSelector);
							$(indicators).each(
									function() {
										var isActive = $(this).hasClass("active");
										var carouselId = $(this)
												.attr('data-target');
										var slideNumber = $(this).attr(
												'data-slide-to');

										var content = $(this).text();

										if (content.trim() == "")
											content = $(this).text(
													parseInt(slideNumber) + 1);

										if (!isActive)
											$(this).attr(
													'onClick',
													'$("' + carouselId
															+ '").carousel('
															+ slideNumber + ');');
										else
											$(this).removeAttr('onClick');
									});
						}
						$(carouselSelector).ready(setIndicators);
						$(carouselSelector).on('slid.bs.carousel', setIndicators);

						$(carouselSelector).on('slide.bs.carousel', function() {
							$('.inputonly', carouselSelector).css({
								'opacity' : 0
							});
							$(headerSelector).css({
								'opacity' : 0
							});

						});

						$(carouselSelector).on(
								'slid.bs.carousel',
								function() {
									
									var currentSlide = $(this).find('.active');
									// console.log(currentSlide);
									var newHeadline = $('.text-sub',
											currentSlide).text();
									// headerSelector.animate({
									// 'opacity' : 0
									// }, 250, function() {
									// $(this).text(newHeadline);
									// }).animate({
									// 'opacity' : 1
									// }, 250);

									$(headerSelector).text(newHeadline);
									carouselPosition();
//temporarily BugFix for Matrix with AttachedOpen for Smartphone									
									if ($(carouselSelector).find(
									".active .text-sub .attached")
									.parent().parent().length > 0) {
										$(headerSelector).css({
											'opacity' : 0
										});
										$('.inputonly', carouselSelector).css({
											'opacity' : 0
										});
									}
									else {
										$(headerSelector).css({
											'opacity' : 1
										});
										$('.inputonly', carouselSelector).css({
											'opacity' : 1
										});
									}
									
								});

						function debug() {
							console.log("debug flip over");
							$('.inputonly', carouselSelector).css({
								'opacity' : 0
							});
							$(this).delay(1000).queue(
									function() {

										$(carouselSelector).trigger(
												'slid.bs.carousel');

										$(this).dequeue();

									});
							$('.carousel-control-prev', controlSelector)
									.unbind('click', debug);
							$('.carousel-control-next', controlSelector)
									.unbind('click', debug);
						}

						function setButtons() {
							var currentSlide = $(carouselSelector).find(
									'.active');

							var prevSlide = currentSlide.prev();
							var prevTitle = $('.text-sub', prevSlide).text();

							var nextSlide = currentSlide.next();
							var nextTitle = $('.text-sub', nextSlide).text();

							var prevBtn = $('.carousel-control-prev .label',
									controlSelector);
							var nextBtn = $('.carousel-control-next .label',
									controlSelector);

							if (!prevTitle) {
								var lastSlide = $('.carousel-item:last',
										carouselSelector);
								// prevTitle = $('.text-sub', lastSlide)
								// .text();
								$('.carousel-control-prev', controlSelector)
										.on('click', debug);
							} else
								$('.carousel-control-prev', controlSelector)
										.unbind('click', debug);

							if (!nextTitle) {
								var firstSlide = $('.carousel-item:first',
										carouselSelector);
								// nextTitle = $('.text-sub', firstSlide)
								// .text();
								$('.carousel-control-next', controlSelector)
										.on('click', debug);
							} else
								$('.carousel-control-next', controlSelector)
										.unbind('click', debug);

							prevBtn.text(prevTitle);
							nextBtn.text(nextTitle);

							prevBtn.parent().show();
							nextBtn.parent().show();
						}

						$(carouselSelector).ready(setButtons);
						$(carouselSelector).on('slid.bs.carousel', setButtons);
						$(carouselSelector).trigger('slid.bs.carousel');

						$(window).resize(function() {
							carouselPosition();
							$('.inputonly', carouselSelector).css({
								'opacity' : 1
							});
						});

						function carouselPosition() {
							$('.inputonly', carouselSelector).css({
								'opacity' : 0
							});

							if ($(carouselSelector).find(
									":not(.active) .text-sub .attached")
									.parent().parent().length > 0) {
								$(carouselSelector).find(
										":not(.active) .text-sub .attached")
										.parent().parent().removeClass(
												"hidden-sm-down").addClass(
												"hidden-sm-down");
							}

							if ($(carouselSelector).find(".text-sub").length > 0) {
								$(carouselSelector).find(".text-sub")
										.removeAttr('style');
								$(carouselSelector).find(".text-sub")
										.removeClass("inputonly");
							}

							if ($(carouselSelector).find(".carousel-inner").length > 0) {
								$(carouselSelector).find(".carousel-inner")
										.removeAttr('style');
							}

							if ($(carouselSelector)
									.find(
											":not(.active) .custom-control-description > span").length > 0) {
								$(carouselSelector)
										.find(
												":not(.active) .custom-control-description > span")
										.removeAttr('style');
							}

							if (window.matchMedia('(max-width: 767px)').matches) {

								if ($(carouselSelector).find(
										".active .custom-control-description").length > 0) {
									$(carouselSelector)
											.find(
													".active .custom-control-description > span")
											.css({
												'overflow-wrap' : 'break-word'
											});
								}

								var headElement = $(headerSelector);

								var headOffset = headElement.offset();
								if (headOffset) {
									var headTopPos = Math.round(headOffset.top);
									var headLeftPos = Math
											.round(headOffset.left);

									// console.log("headOffset : " + headTopPos
									// + " , " + headLeftPos);

									if ($(carouselSelector).find(
											".active .text-sub .attached")
											.parent().parent().length > 0) {
										$(carouselSelector).find(
												".active .text-sub .attached")
												.parent().parent().removeClass(
														"hidden-sm-down");
										
										$(carouselSelector).find(
										".active .text-sub .attached")
										.parent().parent().removeClass(
												"hidden-sm-down");
									}
//temporarily BugFix for Matrix with AttachedOpen for Smartphone
									if ($(carouselSelector).find(
											".active .text-sub").length > 0) {
//										$(carouselSelector).find(
//												".active .text-sub").addClass(
//												"inputonly");
										$(carouselSelector).find(
												'.carousel-inner').css({
											'overflow' : 'visible'
										});
										$(carouselSelector).find(
												'.active .text-sub').css({
											'font-weight' : "600",
											'text-align' : "center", 
											'min-width' : "280px"
											
										});
										var isAndroid = /android/i
												.test(navigator.userAgent
														.toLowerCase());
										var isiDevice = /ipad|iphone|ipod/i
												.test(navigator.userAgent
														.toLowerCase());
										if (isiDevice)
											headTopPos = headTopPos + 5;
										else if (isAndroid)
											headTopPos = headTopPos + 5;
										$(carouselSelector).find(
												'.active .text-sub').offset({
											top : headTopPos-25,
											left : headLeftPos + 15
										});
									}
									
								}
							}
						}
						//
					}
				});

/* adjust pipe length dynamically */

// $(document).ready(function() {
// $('.pipe-zofar-item').parent().addClass('pipe-zofar-item-parent');
// });
$('.pipe-zofar-item').parent().ready(function() {
	$('.pipe-zofar-item').parent().addClass('pipe-zofar-item-parent');
});

function stringGen(len) {
	var text = "";
	var charset = "abcdefghijklmnopqrstuvwxyz0123456789";
	for (var i = 0; i < len; i++)
		text += charset.charAt(Math.floor(Math.random() * charset.length));

	return text;
}

function pipeCalc() {
	var scrollTop = $(window).scrollTop();
	var scrollLeft = $(window).scrollLeft();
	var parentObjs = $('.add-pipe');

	$.each(parentObjs, function(index, parentObj) {
		var items = $(parentObj).find('.pipe-zofar-item-parent');
		var item = items.last();

		if ((typeof item !== "undefined") && (item !== null)) {
			var flexOffset = $(parentObj).offset();
			var itemOffset = $(item).offset();
			if ((typeof itemOffset !== "undefined") && (itemOffset !== null)) {
				var diffTop = Math.round((itemOffset.top - scrollTop)
						- (flexOffset.top - scrollTop));
				var diffTopPercent = Math.round((diffTop / $(item)
						.offsetParent().height()) * 100);

				var diffLeft = Math.round((itemOffset.left - scrollLeft)
						- (flexOffset.left - scrollLeft));
				var diffLeftPercent = Math.round((diffLeft / $(item)
						.offsetParent().width()) * 100);

				var diffPercent = Math.max(diffTopPercent, diffLeftPercent);

				$(parentObj).removeClass(
						function(index, className) {
							return (className.match(/(^|\s)pipe-\S+/g) || [])
									.join(' ');
						});

				$(parentObj).addClass("pipe-" + diffPercent);
			}
		}
	});
}

$('.pipe-zofar-item').parent().ready(function() {
	pipeCalc();// run when page first loads
});


$(window).resize(function() {
	pipeCalc();// run on every window resize
});

$('.carousel').on('slid.bs.carousel', function() {
	pipeCalc();// run on every slide change
});


/*
 * 	To switch on/off of tooltips: activate/deactivate following tooltips function
 * 
 * */

//function tooltips() {
//	if (window.matchMedia('(max-width: 767px)').matches) {
//		$('[placeholder]').tooltip({
//			placement : function(context, element) {
//				var position = $(element).position();
////				console.log(position.top - $(window).scrollTop());
//				if (position.top - $(window).scrollTop() < 10) {
//					return "bottom";
//				}
//				return "top";
//			},
//			trigger : 'hover',
//			container : 'body'
//		});
//	} else {
//		$('[placeholder]').tooltip('dispose');
//	}
//}

function tooltips() {
//	if (window.matchMedia('(max-width: 767px)').matches) {
//		$('[placeholder]').tooltip({
//			placement : function(context, element) {
//				var position = $(element).position();
////				console.log(position.top - $(window).scrollTop());
//				if (position.top - $(window).scrollTop() < 10) {
//					return "bottom";
//				}
//				return "top";
//			},
//			trigger : 'hover',
//			container : 'body'
//		});
//	} else {
//		$('[placeholder]').tooltip('dispose');
//	}
	
	//$('[placeholder]').attr("placeholder","");
}

$(document).ready(function() {
	tooltips();
});

$(window).resize(function() {
	tooltips();
});

$(window).scroll(function() {
	tooltips();
});

$(".headerAcc").click(function() {

	$header = $(this);
	// getting the next element
	$content = $header.next();
	// open up the content needed - toggle the slide- if visible, slide up, if
	// not slidedown.
	$content.slideToggle(500, function() {
		$header.addClass("active");
		// execute this after slideToggle is done
		// change text of header based on visibility of content div
		$header.text(function() {
			if (!($content.is(":visible"))) {
				$header.removeClass("active");
			}
		});
	});
});


//Dbl Matrix Header Bugfix

function dblMatrixHeaderBugfix() {
	
    $(".form-inner.pt-2").each(function() {
//    	console.log($(this));
    	$(this).removeAttr("style");
    	if (window.matchMedia('(max-width: 575px)').matches) {
			if( $(this).has(".dbl-matrix-rdc").length ){
				$(this).attr("style","padding-top: 3rem !important");		
			}
		}
  });
};

$(document).ready(function() {
	dblMatrixHeaderBugfix();
});

$(window).resize(function() {
	dblMatrixHeaderBugfix();
});


function changePretestHeader() {
	header = $('header[id*=textcomment][class=question-main]');
	headerText = $('div[class=question-text]',$(header));
	
	var newTitle= "weitere Kommentare / Anmerkungen";
	$(headerText).text(newTitle);
}

$(document).ready(function() {
	changePretestHeader();
});



// $(document).ready(function() {
// $('[placeholder]').tooltip({
// placement : function(context, element) {
// var position = $(element).position();
// console.log(position.top - $(window).scrollTop());
// if (position.top - $(window).scrollTop() < 110) {
// return "bottom";
// }
// return "top";
// },
// trigger : 'hover',
// container : 'body'
// });
// });

// $(function(){
// var options = {
// placement: function (context, element) {
// var position = $(element).position();
// console.log(position.top - $(window).scrollTop());
// if (position.top - $(window).scrollTop() < 110){
// return "bottom";
// }
// return "top";
// },
// trigger: "hover",
// container : 'body'
// };
// $('[placeholder]').tooltip(options);
// });

// // repositioning of attached open in small screen
// function repositionAttached() {
// if (window.matchMedia('(max-width: 767px)').matches) {
// var attached = $('.attached');
// if (attached.length > 0) {
// attached.css({
// 'top' : attached.prev().outerHeight() + 'px'
// });
// }
// } else
// $('.attached').css({
// 'top' : '0px'
// });
//
// }
//
// function repositionAttached1() {
// if (window.matchMedia('(max-width: 767px)').matches) {
// var attached = $('.attached');
//
// if (attached.length > 0) {
// var beforeElem = attached.prev();
//
// if (beforeElem.length > 0) {
// beforeElem.find(".pos-marker").each(function() {
// $(this).remove();
// });
//
// var marker = '<span class="pos-marker">keks</span>';
//
// var text = beforeElem.html();
// text += marker;
// beforeElem.html(text); // Set wrapper
// }
//
// var offset = beforeElem.find(".pos-marker").offset();
// var attachedOffset = attached.offset();
// if (offset) {
// var offsetTopPos = Math.round(offset.top);
// var offsetLeftPos = Math.round(offset.left);
//
// // attached.animate({
// // left : (offsetLeftPos - attachedOffset.left),
// // top : (offsetTopPos - attachedOffset.top)
// // });
// attached.css({
// 'top' : (offsetTopPos - attachedOffset.top) + 'px',
// 'left' : (offsetLeftPos - attachedOffset.left) + 'px'
// });
// }
//
// console.log("pos " + offset.top + " , " + offset.left + " ("
// + attachedOffset.top + "," + attachedOffset.left + ")");
//
// }
//
// } else
// $('.attached').css({
// 'top' : '0px',
// 'left' : '0px'
// });
//
// }
//
// $(document).ready(function() {
// // repositionAttached1();
// });
// $(window).resize(function() {
// // repositionAttached1();
// });

function repositionMissing() {
	 if (window.matchMedia('(max-width: 767px)').matches) {
		 $('.row').each(function() {
			   var select = $('.dropDownsmall select',this);
			   if ((typeof select != "undefined") && (select != null)){
				   var offset = $(select).offset();
				   
				   var height = $(select).height();
			   
					if (offset) {
						var missing = $('.custom-control',this);
						var missingOffset = $(missing).offset();

						missing.css({
						 'top' : (offset.top - missingOffset.top +height + 15) + 'px',
						 'left' : (offset.left - missingOffset.left) + 'px'
						 });
					}
			   }		    
		 });
	} else{
		 $('.row').each(function() {
			   var select = $('.dropDownsmall select',this);
			   if ((typeof select != "undefined") && (select != null)){
				   var missing = $('.custom-control',this);
				   if ((typeof missing != "undefined") && (missing != null)){
					missing.css({
						 'top' : 0 + 'px',
						 'left' : 0 + 'px'
						 });
				   }
			   }		    
		 });

	}

}

	 $(document).ready(function() {
	  repositionMissing();
	 });
	 $(window).resize(function() {
	  repositionMissing();
	 });

$(document).ready(function() {
	$('.openmatrix label').each(function(context, element) {
		var prefixSelector = $(".prefix", this);
		var inputSelector = $("input[type]", this);
		var errorSelector = $("span.error", this);
		
    	$(inputSelector).removeAttr("style");
    	$(errorSelector).removeAttr("style");
    	
    	if (window.matchMedia('(min-width: 48em)').matches) {
			if( $(errorSelector).length ){
				var text = $(errorSelector).html();
				$(errorSelector).attr("style","display:none");
				$(inputSelector).attr("title",text);
				
				$(inputSelector).tooltip({
					placement : "top",
					trigger : "manual",
					container : "body"
				}).tooltip('show');
				
				var tooltipId = $(inputSelector).attr("aria-describedby");		
				$("#"+tooltipId).addClass("zofar_openmatrix_error_tooltip");
			}
		}
	});	
});

//$('.custom-p01_07').ready(function(context, element) {
//	var selectSelector = $('select', this);
//	var emptySelector = $('optgroup:empty',selectSelector);
//	var notemptySelector = $('optgroup:not(:empty)',selectSelector);
//
//	//$(emptySelector).attr("style","display:none");
//	$(emptySelector).remove();
//	
//	var $optgroup = $(notemptySelector).children().clone();
//	selectSelector.append($optgroup);
//	$(notemptySelector).remove();
//});	



//set placeholder on pages that match the pattern (here e.g. custom-B1,custom-B3a,custom-B3b and custom-CMI2,custom-CMI3)
// !!! EDIT following 2 variables
 var pattern = "custom-test_[singlechoice2|multiplechoice2]";

//else clear placeholder and add prefix before field as <span class="zofar-attached-prefix">placeholder text</span>
$(document).ready(function() {
	var pattern = "custom-test_[singlechoice2|multiplechoice2]";
	var postfixPattern = "";

	var bodySelector = $('body',this).filter(function(){return !$( this ).attr( "class" ).match(pattern);});
//	$('[placeholder]',bodySelector).each(function() {
//		var parentSelector = $( this ).parent();
//		if ($(parentSelector).parents('.openmatrix').length === 0) {
//			var old_prefix = $(".prefix",parentSelector);
//			$(old_prefix).attr("style","display:none");
//			$( "<span class=\"zofar-attached-prefix\">"+$( this ).attr("placeholder")+"</span>" ).insertBefore( this );
//		}
//		$( this ).attr("placeholder","");
//	});
	
	var inversebodySelector = $('body',this).filter(function(){return $( this ).attr( "class" ).match(pattern);});
	$('[placeholder]',inversebodySelector).each(function() {
		var parentSelector = $( this ).parent();
		if ($(parentSelector).parents('.openmatrix').length === 0) {
			var old_prefix = $(".prefix",parentSelector);
			$(old_prefix).attr("style","display:none");
		}
	});
//	var inversePostfixBodySelector = $('body',this).filter(function(){return $( this ).attr( "class" ).match(postfixPattern);});
//	$('[type=text]',inversePostfixBodySelector).each(function() {
//		var parentSelector = $( this ).parent();
//		if ($(parentSelector).parents('.openmatrix').length === 0) {
//			console.log($(this));
//			var old_prefix = $(".prefix",parentSelector);
//			$(old_prefix).removeAttr("style");
//			$( this ).removeAttr("placeholder");
//			var old_postfix = $(".postfix",parentSelector);
//			$(old_postfix).attr("style","display:none");
//			var old_postfix_value = $(".xx",old_postfix);
//			var text = $(old_postfix_value).html();
//			$( this ).attr("placeholder",text);
//		}
//	});
});


//$(document).ready(function() {
//	var pattern = "custom-D3_6";
//
//	var bodySelector = $('body',this).filter(function(){return !$( this ).attr( "class" ).match(pattern);});
//	$('[placeholder]',bodySelector).each(function() {
//		var parentSelector = $( this ).parent();
//		
//		if ($(parentSelector).parents('.openmatrix').length === 0) {
//			var old_prefix = $(".prefix",parentSelector);
//			$(old_prefix).attr("style","display:none");
//			$( "<span class=\"zofar-attached-prefix\">"+$( this ).attr("placeholder")+"</span>" ).insertBefore( this );
//			
//		}
//		$( this ).attr("placeholder","");
//	});
//});


/*
 *	Activate/Deactivate of indicators depending on media query for questions with units
 * 
 */		
//	$('.custom-PAGE .carousel').each(
//			function() {
//				if (window.matchMedia('(max-width: 767px)').matches) {
//					var carouselSelector = $(this);
//					var innerSelector = $(".carousel-inner", carouselSelector);
//					var indicatorSelector = $(".carousel-indicators", carouselSelector);
//					var fieldsets = $("fieldset", carouselSelector);
//					$(fieldsets).each(function() {
//						var fieldSetSelector = $(this);
//						var items = $('.row', fieldSetSelector);
//						$(items).each(function() {
//							var item = $(this);
//							$(fieldSetSelector).before(item);
//						});
//						$(fieldSetSelector).remove();
//					});
//					var lft = 0;
//					indicatorSelector.empty();
//					$(".row",innerSelector).each(function() {
//						var item = $(this);
//						var li = $( "<li></li>" ).text(lft+1);
//						if(lft == 0)li.addClass("active");
//						li.attr("data-target","#"+carouselSelector.attr("id"));
//						li.attr("data-slide-to",lft);
//						li.appendTo( indicatorSelector );
//						lft = lft + 1;
//					});
//				}
//			}
//	);

	
	
	
$(".custom-PAGE UID_inputfield input").keypress( function(e) {
    var chr = String.fromCharCode(e.which);
    if (".,;:".indexOf(chr) < 0)
        return true;
    return false;
});

