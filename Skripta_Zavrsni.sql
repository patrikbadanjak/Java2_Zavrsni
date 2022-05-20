create database Java2_Zavrsni

go

use Java2_Zavrsni

go

create table Restaurant
(
	IDRestaurant int primary key identity,
	RestaurantName nvarchar(50) not null,
	RestaurantAddress nvarchar(50) not null,
	TelephoneNumber nvarchar(15)
)

go

create table RestaurantTable
(
	IDTable int primary key identity,
	RestaurantID int foreign key references Restaurant(IDRestaurant),
	TableNumber tinyint not null
)

go

create table Item
(
	IDItem int primary key identity,
	ItemName nvarchar(30) not null,
	Stock smallint not null,
	Price money not null
)

go

create table UserRole
(
	IDRole int primary key identity,
	RoleName nvarchar(15) not null
)

go

create table Employee
(
	IDEmployee int primary key identity,
	FirstName nvarchar(25) not null,
	LastName nvarchar(25) not null,
	Email nvarchar(30) not null unique,
	Pwd nvarchar(128) not null,
	RoleID int foreign key references UserRole(IDRole)
)

go

create table FoodOrder
(
	IDOrder int primary key identity,
	TableID int foreign key references RestaurantTable(IDTable),
	NumberOfPeople tinyint not null,
	OrderStatus tinyint not null,
	EmployeeID int foreign key references Employee(IDEmployee),
	TimeOfOrder datetime not null
)

go

create table OrderedItem
(
	OrderID int foreign key references FoodOrder(IDOrder),
	ItemID int foreign key references Item(IDItem),
	Quantity smallint not null
)

go

create table Menu
(
	IDMenu int primary key identity,
	IsActive bit not null,
	MenuName nvarchar(15) not null
)

go

create table MenuItem
(
	ItemID int foreign key references Item(IDItem),
	MenuID int foreign key references Menu(IDMenu)
)

go

create proc LoginUser
	@email nvarchar(30), @password nvarchar(128)
		as
			select IDEmployee, FirstName, LastName, Email, RoleID
			from Employee
			where Pwd = @password

go

create proc GetItems
	AS
		select IDItem, ItemName, Stock, Price
		from Item

go

create proc GetItem
	@idItem int
		as
			select ItemName, Stock, Price
			from Item
			where IDItem = @idItem

go

insert into UserRole(RoleName)
values ('Waiter'), ('Manager')

go

insert into Item(ItemName, Price, Stock)
values
	('Chicken soup', 9.99, 12),
	('Chicken wings', 17.99, 9),
	('Ribs', 19.99, 10),
	('Pulled pork', 24.99, 15),
	('Pizza', 34.99, 12),
	('Beef tortilla', 29.99, 5),
	('Cheese quesadilla', 9.99, 12),
	('Kebab tortilla', 33.99, 11),
	('Coca-Cola 0.5l', 9.99, 14),
	('Schweppes tangerina 0.5l', 9.99, 14),
	('Potato salad', 12.99, 8),
	('Stir fry', 24.99, 7),
	('Fried rice', 19.99, 5)

go

insert into Employee(FirstName, LastName, Email, Pwd, RoleID)
values('Patrik', 'Badanjak', 'pbadanjak@gmail.com', 'E239F67756BBA3AF660E4226C340183A9CA4BDC40038C0CFDEA2FBAA59605BE32548DF2535E5A9F9CEEDB12D9666C6FB153ADA99830ED5CD84EB0C2C4D00260A', 2),
('Franko', N'Papić', 'Frankopapic@gmail.com', '790C079EBBC99C4B053FCCFA20BBE5A99FAA34099F2E4CA1208107EF97145BACD233FF4B7225E503E34733D8C789AEB400210767DF9AE6F70ACE2D4C73630EF9', 1)

go

insert into Restaurant(RestaurantName, RestaurantAddress, TelephoneNumber)
values('Papizza', 'Ilica 19A', '014234233')

go

DECLARE @first AS INT = 1
DECLARE @last AS INT = 25

WHILE(@first <= @last)
BEGIN
    INSERT INTO RestaurantTable(RestaurantID, TableNumber)
	VALUES(1, @first)
    SET @first += 1
END
