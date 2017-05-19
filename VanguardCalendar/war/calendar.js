$(document).ready(function() {
	options = getOptions()
	InitiateCalendar(options);
});
function getOptions()
{
	var date = new Date();
	var d = date.getDate();
	var m = date.getMonth();
	var y = date.getFullYear();
	return 	{
				contentHeight			: 	$(window).height()-100,
				defaultView				: 	'agendaWeek',//resourceDay
				header					: 	{
												left		: 	'prev,next today',
												center		: 	'title',
												right		: 	'agendaWeek,resourceDay',
											},
				buttonText				:	{
											    prev		:	'&lsaquo;', // <
											    next		:	'&rsaquo;', // >
											    prevYear	:	'&laquo;',  // <<
											    nextYear	:	'&raquo;',  // >>
											    today		:	'Today',
											    month		:	'Monthly View',
											    week		:	'Weekly View',
//											    day			:	'day',
											    resourceDay	:	'Daily View',	
											},
				slotMinutes				:	15,
				firstHour				: 	8,
				firstDay				:	1,
				allDaySlot				:	false,
				allDay					:	false,
				selectable				: 	true,
				selectHelper			: 	true,
				editable				: 	true,
				disableDragging			:	false,
				disableResizing			:	true,
			 
				events					: 	[
												{
													id: 999,
													title: 'Repeating Event',
													start: new Date(y, m, d-3, 16, 0),
													allDay: false,
													type:'appointment',
												},
												{
													id: 999,
													title: 'Repeating Event',
													start: new Date(y, m, d+4, 13, 0),
													allDay: false,
													className:'maroon',
													type:'appointment',
													
												},
												{
													title: 'Meeting',
													start: new Date(y, m, d, 14, 30),
													allDay: false,
													className:'green',
													type:'appointment',
													resourceId:5,
												},
												{
													title: 'Lunch with a VIP',
													start: new Date(y, m, d, 12, 0),
													end: new Date(y, m, d, 14, 0),
													allDay: false,
													className:'red',
													type:'appointment',
													resourceId:1
												},
												{
													title: 'Blocked',
													start: new Date(y, m, d, 10, 0),
													end: new Date(y, m, d, 12, 0),
													allDay: false,
													className:'blocked',
													type:'block',
													resourceId:1
													
												},
												{
													title: 'Birthday Party',
													start: new Date(y, m, d+1, 10, 0),
													end: new Date(y, m, d+1, 12, 30),
													allDay: false,
													className:'light-blue',
													type:'appointment',
												},
												{
													title: 'Click for Google',
													start: new Date(y, m, 28),
													end: new Date(y, m, 29),
													url: 'http://google.com/',
													type:'appointment',
												}
											],
				selectedResource		:	"1",
				unAvailability			:	function(start,end,callback)
											{
												console.log(start);
												console.log(end);
												var timeoff = {};
												timeoff[1] = {};
												timeoff[1][start.getTime()] = 	[
													                              	 {
													                              		start		:	28800,
													                              		end			:	61200,
													                              		isOpen		:	true
													                              	 },
																					 {
																						 start		:	32400,
																						 end		:	36000,
																					 },	
																					 {
																						 start		:	39600,
																						 end		:	43200,
																					 },
																				];
		//												timeoff[1][end.getTime()]	=	[];
												callback(timeoff);
											},
				select					:	function( startDate, endDate, allDay, jsEvent, view, resouce )
											{
		//												$('#calendar').fullCalendar("unselect");
												window.event = {};
												window.event.startDate = startDate;
												window.event.endDate 	= endDate;
												window.event.allDay 	= allDay;
												window.event.jsEvent 	= jsEvent;
												window.event.view 		= view;
												window.event.resouce 	= resouce;
												console.log("event::: ");
												console.log(window.event);
											},
				 eventClick				: 	function(event, element) 
				 							{
					 							console.log(event);
					 							console.log(element);
		//					 							event.title = "CLICKED!";
					 								
		//					 							$('#calendar').fullCalendar('updateEvent', event);
				 							},
				
		}
}

function InitiateCalendar()
{
	var calendar = $('#calendar');
	calendar.fullCalendar('destroy');
	calendar.fullCalendar(options);	
}