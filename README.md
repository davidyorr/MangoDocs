MangoDocs
=========
Documentation generator for JavaScript files.

Syntax
------
Use `//` style commenting in your code, rather than `/* */`.

The documentation is specified by annotations,
which all begin with `@`.

Child entries (e.g. method of a class, subclass, etc.) can not
be added until the parent entry has been added.

Each entry in the documentatiom consists of up to three parts :

##### 1. The Type

The first part of an entry is the type defintion. This is
the only required part. The type can be any of the following :

- `@class`
- `@event`
- `@instanceproperty`
- `@instancemethod`
- `@namespace`
- `@staticproperty`
- `@staticmethod`

##### 2. The Description

After the type is the optional description. The
description can span any number of lines, and should
contain no annotations.

> `// The entry's description directly after the type definition`

Under class entries, a specific description for the constructor
can be specified with the `@constructor` annotation.

    // @class Person
    // Description of Person objects.
    // @constructor Decsription of the Person constructor,
    // which can span multiple lines.

##### 3. Parameter(s) and return value

The last part of an entry is the parameters and return value,
which are optional and can be in any order. They are specified by :

- `@param`
- `@return`

A parameter definition consists of a type, name, and description:
> `{type} name : description`

A return definition consists of a type and description:
> `{type} : description`

##### Other syntax options

For method entries, use hash `#` notation to declare what class or
namespace the method belongs to :

> `@staticmethod Circle#clear`

> `@instancemethod Triangle#draw`

For static property entries and subclass or subnamespace entries, use dot
`.` notation to declare what class or namespace the property belongs
to :

> `@staticproperty Circle.width`

> `@namespace Circle.defaults`

> `@staticproperty Circle.defaults.radius`

An instance property entry does not need to explicitly declare itself
as `@instanceproperty`.

They can all be declared at once :

    // @namespace Rectangle.defaults
    // Default options for rectangle objects.
    // {int} width : the width of the rectangle {@default 50}
    Rectangle.defaults = {
        width : 50,
        ...
    }

Or can also be declared inline with the code :

    // @class Triangle
    // A circle
    function Triangle() {
        this.x = 50, // {int} width : the width of the triangle
        ...
    }

And if there is no description, simply end with the colon :

    this.x = 50, // {int} width :

Additional information about the instance method can be added on the next line
by prepending with a colon :

    this.x = x, // {int} width : the width of the triangle
                // : must be in the inclusive range of [50, 200]

For `@staticproperty` the name is not repeated when specifying
the type and description :

    // @staticproperty Circle.maxRadius
    // {int} The maximum allowed radius for a circle.
    Circle.maxRadius = 500

To link to another class, wrap `< >` around the word :

>`Draws a <Circle> onto the canvas element.`

Certain HTML tags may also be used, which means that the code that
is being documented can't have a class name with a reserved HTML tag.
The reserved words are :

- `p`
- `tt`

Any number of example code blocks can be included using `@example`
for each definition. Examples can span multiple lines. For
examples with multiple lines, start the example on the next line
and not on the same line as the `@example` annotation.

    // @instancemethod Square#draw
    // Draws the square onto the canvas element
    // @param {int} x : x axis
    // @param {int} y : y axis
    // @example square.draw(50, 50);
    // @example
    // var square = new Square();
    // square.draw(100, 50);

#### An example class entry :

    // @class Circle
    // Represents a circle on an HTML5 canvas element.
    // @param {radius} : the radius of the circle

#### An example instance method entry :

    // @instancemethod Circle#height
    // Returns the circle's height.
    // @return {int} : height of the circle

#### An example with javascript code included :

    // ------------------------------------------------------
    // @class Square
    //
    // Represents a square on an HTML5 canvas element.
    //
    // @param {DOM} element  : the canvas element
    // @param {int} diameter : the diameter of the square
    // ------------------------------------------------------
    function Square(element, diameter) {
                                                // These are instance properties being  declared inline
        this.diameter = diameter;               // {int}     diameter : the diameter of the square
        this.canvas = element;                  // {DOMNode} canvas   : the canvas element
        this.ctx = element.getContext('2d');    // {CanvasRenderingContext2D} ctx : the canvas's rendering context

        // @event Square.onhover
        //
        this.canvas.onhover = Square.Events.onhover;
        // @event Square.onmousedown
        // Mouse down handler
        this.canvas.onmousedown = Square.Events.onmousedown;
    }

    // @instancemethod Square#draw
    // Draws the square onto the canvas.
    // ------------------------------------------------------
    Square.prototype.draw = function() {
        ...
    }

    Square.Events = {
        onhover     : ...
        onmousedown : ...
        ...
    }

    // @namespace
    // Default properties for Notes
    Square.defaults {

    }

#### Complete list of annotations (and their shorthands)

    @author
    @class
    @constructor        [@ctor]
    @event              [@evt]
    @example            [@ex]
    @instancemethod     [@im]
    @instanceproperty   [@ip]
    @param
    @return             [@ret]
    @staticmethod       [@sm]
    @staticproperty     [@sp]

Usage
-----
> `java -jar mangodocs.jar -i input/directory -o output/directory [optional args]`