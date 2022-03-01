## Solution Details

The project expects use of JDK11 and uses Maven as a build framework.
Dependencies are predominantly Spring Boot and JUnit 5.

`mvn clean verify` will run all tests.

`mvn clean spring-boot:run` will start the application on port 8080. Logging should show the behaviour

## Design Approach

The assignment divides into 2 parts, one is the problem domain and algorithm for calculating change, and the other is creating the application and REST api.
The java packaging honours this divide.
This could be further improved by making the maven project multi-module.

The model package contains business logic. The 3 key interfaces in this package are:
- `IFloat` which defines how the machine holds a set of coins, plus it's initialisation/operation
- `IOfferedAmount` which is a stateful container for a set of coins added by the client/user. The amount is extended one coin at a time
- `ISalesTransaction` which orchestrates an attempt to make a sale, collecting the offered coins, determining coin change required

The underlying algorithm is possibly naive but seems to work pretty well. Basically if sufficient funds have been 
deposited and change is required, the steps are -
- from the Kitty, derive a subset of coin denominations for possible change. This handles the absence of some coins, and ignores coins of
too great a value. Return them in an ordered list (high to low)
- from this list, derive all possible ordering of the denominations. This ignores amount of coin per denomination since always >0
- the variant list is cached to avoid needless rework which is fine since it deals with denominations only
- Iterate through the variants. The ordered list is always first since that has highest chance of success
- Predict what happens if we iteratively remove coins from the kitty according to the variant, after each coin re-caculate remaining change needed
- If the variant can build the change, then break the loop and make the transaction. Otherwise try the next variant
- If all variants exhausted, then no change is possible, the sale is cancelled


## Rest API design

The API is split into two controllers for segregation of concerns.
The REST operations are extremely basic. The assignment makes no comment about REST style e.g. OpenAPI conformance.

### Admin Operations

`MachineAdminController` handles initialisation functions including setting initial float coins.
The URI path has prefix which could for example be secured separately to sales.

GET /admin/init   returns HTTP 200 text/plain instructions on initialisation.

`curl -v  "http://localhost:8080/admin/init"`

POST /admin/init  expects a query string comprising known coin denominations as keys, and count of coins per value. Returns
HTTP 200 with JSON representation of the float.

`curl -v -X POST "http://localhost:8080/admin/init?C100=2&C50=1&C2=1&C1=10"`

POST /admin/init if supplying unrecognised denominations will have no effect on state and return HTTP 400

`curl -v -X POST "http://localhost:8080/admin/init?EUR50=2&C2=1&C1=10"`

GET /admin/kitty  will return HTTP 200 with JSON representation of the current state of the float

`curl -v "http://localhost:8080/admin/kitty"`

### Sales Operations

`SalesController` allows for clients to add coins and attempt a sales transaction.

POST /addFunds/ expects a query param of name "coin" and value match permissible Denominations. On success HTTP 200 with JSON
representation of the current aggregated funds supplied.

```
$ curl -X POST "http://localhost:8080/addFunds/?coin=C50"
{"coins":{"50":1}}
```

I guess arguably that should be a PATCH.
Supplying a value not recognised by Denominations will get an HTTP 400

POST /makesale/{baskettotal}  attempts to perform the transaction.
Path parameter baskettotal is required and must be an integer value (representing pence amount of sale)

If insufficient funds have been entered, receive HTTP 400. This does *not* cancel the transaction.

```
$ curl -X POST "http://localhost:8080/makesale/980"
{"success":false,"outcome":"please add more funds ","change":{}}`
```
If exact change has been supplied, receive an HTTP 200 success JSON message with no indication of change returned

```
$ curl -X POST "http://localhost:8080/makesale/15"
{"success":true,"outcome":"Thanks for your custom","change":{}}`
```
If sufficient funds but change is returned, again an HTTP 200 with JSON message, note the change field indicates type and amount of change given

```
$ curl -X POST "http://localhost:8080/makesale/5"
{"success":true,"outcome":"sale with change","change":{"5":1}}`
```
The edge case will be a circumstance where the machine cannot honour the sale due to insufficient float coins.
In this situation it is HTTP 500 with JSON indicating false outcome. The transaction is aborted hence all the added funds returned as change

```
$ curl -X POST "http://localhost:8080/makesale/9"
{"success":false,"outcome":"Sale impossible with current funds in machine","change":{"50":2}}`
```

## Engineering
Unit tests should give acceptable levels of coverage. My IDE indicates
- Class 100%
- Method 94%
- Line 93%

There's a few further validation tests that could be written on the API.

TODO comments make indicate observations along the way.

This application is not designed for concurrency of any form.
In terms of operation, the least optimised aspect is the algorithm handling permutations to obtain
change. This is an in memory map of array variants, many I suspect are redundant. There's no attempt to 
limit the map size though given the number of coin permutations the overall maximum is finite.

Some parts of the code may benefit from JDK17 language features.

## Testing

As requested the Integration Test can be instructive to extend for testing so edit accordingly and
run `mvn clean verify` to trigger Failsafe tests.
Alternatively take inspiration from the example curl commands above

Unit testing the algorithm - see `TestSalesTransaction.changePermutations` which is a repeated test
with randomised input to give some breadth of testing.
Property or fuzz testing like PIT Mutation testing would be an alternative.

## Meta

Code created by Tim Fulcher 22/02/22
trfulcher@googlemail.com
