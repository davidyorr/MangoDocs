$(document).ready(function() {
    var dontCollapse = false;
    // var origColor = 'rgb(106, 159, 180)';
    // var summaryTableHeader = $(".summaryTableHeader")[0];
    // var $summaryTableHeader = $($(".summaryTableHeader")[0]);
    // var th = $($(".summaryTableHeader")[0]).children('th')[0];
    // var $th = $($($(".summaryTableHeader")[0]).children('th')[0]);
    var origColor = $($($(".summaryTableHeader")[0]).children('th')[0]).css('background-color');
    // console.log(color);
    // // console.log(($($(".summaryTableHeader")[0]).children('th')[0]).css('background-color'));

    $(".summaryTableHeader").click(function(e) {
        if (dontCollapse) {
            dontCollapse = false;
        } else {
            body = $(this).parent().children(".summaryTableBody");
            // console.log(body);

            body.toggle();
            // console.log($(this));
            // console.log($(this).css('background'));
            // $(this).css('background', 'red');
            var bColor = $($(this).children('th')[0]).css('background-color');
            if (bColor === origColor) {
                // $($(this).children('th')[0]).css('background-color', '#93B4C1');
                $($(this).children('th')[0]).css('background-color', lighterColor(origColor, .15));
            } else {
                $($(this).children('th')[0]).css('background-color', origColor);
            }
            // console.log($($(this).children('th')[0]).css('background-color'));
            // console.log(this);
            // console.log(this.style.backgroundColor);
        }
    });

    $("a").click(function() {
        dontCollapse = true;
    });

    $('.nestedNamespaces').hide();

    $('.namespaceFold').click(function(e) {
        $(this).toggleClass("entypo-right-open").toggleClass("entypo-down-open");
        $(this.parentElement.childNodes[2]).toggle();
    });
});

var pad = function(num, totalChars) {
    var pad = '0';
    num = num + '';
    while (num.length < totalChars) {
        num = pad + num;
    }
    return num;
};

// Ratio is between 0 and 1
var changeColor = function(color, ratio, darker) {
    // Trim trailing/leading whitespace
    color = color.replace(/^\s*|\s*$/, '');

    // Expand three-digit hex
    color = color.replace(
        /^#?([a-f0-9])([a-f0-9])([a-f0-9])$/i,
        '#$1$1$2$2$3$3'
    );

    // Calculate ratio
    var difference = Math.round(ratio * 256) * (darker ? -1 : 1),
        // Determine if input is RGB(A)
        rgb = color.match(new RegExp('^rgba?\\(\\s*' +
            '(\\d|[1-9]\\d|1\\d{2}|2[0-4][0-9]|25[0-5])' +
            '\\s*,\\s*' +
            '(\\d|[1-9]\\d|1\\d{2}|2[0-4][0-9]|25[0-5])' +
            '\\s*,\\s*' +
            '(\\d|[1-9]\\d|1\\d{2}|2[0-4][0-9]|25[0-5])' +
            '(?:\\s*,\\s*' +
            '(0|1|0?\\.\\d+))?' +
            '\\s*\\)$'
        , 'i')),
        alpha = !!rgb && rgb[4] != null ? rgb[4] : null,

        // Convert hex to decimal
        decimal = !!rgb? [rgb[1], rgb[2], rgb[3]] : color.replace(
            /^#?([a-f0-9][a-f0-9])([a-f0-9][a-f0-9])([a-f0-9][a-f0-9])/i,
            function() {
                return parseInt(arguments[1], 16) + ',' +
                    parseInt(arguments[2], 16) + ',' +
                    parseInt(arguments[3], 16);
            }
        ).split(/,/),
        returnValue;

    // Return RGB(A)
    return !!rgb ?
        'rgb' + (alpha !== null ? 'a' : '') + '(' +
            Math[darker ? 'max' : 'min'](
                parseInt(decimal[0], 10) + difference, darker ? 0 : 255
            ) + ', ' +
            Math[darker ? 'max' : 'min'](
                parseInt(decimal[1], 10) + difference, darker ? 0 : 255
            ) + ', ' +
            Math[darker ? 'max' : 'min'](
                parseInt(decimal[2], 10) + difference, darker ? 0 : 255
            ) +
            (alpha !== null ? ', ' + alpha : '') +
            ')' :
        // Return hex
        [
            '#',
            pad(Math[darker ? 'max' : 'min'](
                parseInt(decimal[0], 10) + difference, darker ? 0 : 255
            ).toString(16), 2),
            pad(Math[darker ? 'max' : 'min'](
                parseInt(decimal[1], 10) + difference, darker ? 0 : 255
            ).toString(16), 2),
            pad(Math[darker ? 'max' : 'min'](
                parseInt(decimal[2], 10) + difference, darker ? 0 : 255
            ).toString(16), 2)
        ].join('');
};
var lighterColor = function(color, ratio) {
    return changeColor(color, ratio, false);
};
var darkerColor = function(color, ratio) {
    return changeColor(color, ratio, true);
};