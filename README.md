# HousekeepingRoomListAssigner GUI application 
Housekeeping Room List Assigner application allows hotel operators to 
- create housekeeping room list assignment using checboxes to mark selected room numbers as checked-out or stayover.
- view updated list in data table model, sort list in data table by room number, room type, and its status.
- remove selected rows from data table
- add employee name and current working date
- use optional daily wage calculation feature

Dependencies:
- ListIntegratorLibary (https://github.com/hbhakta2/ListIntegratorLibrary)
- Create folder path "C:\HousekeepingReportCreatorAppFiles\RoomConfiguration"
- Run HotelRoomListConfigurator application once, and add at least one room, roomtype, save & close (https://github.com/hbhakta2/HotelRoomListConfigurator)

Features:
- North panel:- 1) employee name label and field, 2) Date label and field, 3) SAVE button
- West panel:- 1) List of rooms with checkboxes,
             - 2) Mark as CHECKED OUT button,
             - 3) Mark as STAY OVER button
- Central panel:- 1) Data Table Model to display list by Room number, Type, and Status
- East panel: 1) Remove button to remove selected room/s from Data Table Model
- South panel (optional wage calculator feature):
              - 1) Checkbox to activate this feature,
              - 2) C/O Wage label and input field,
              - 3) S/O Wage label and input field
