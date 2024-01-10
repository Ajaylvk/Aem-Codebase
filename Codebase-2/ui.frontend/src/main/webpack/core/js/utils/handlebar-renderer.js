/* eslint-disable */
import $ from 'jquery';
import Handlebars from 'handlebars';

export default class RenderTemplate {
    constructor() {
        this.$el = '';
        this.template = '';
        this.helpers();
    }

    init(ele, data, append) {
        this.$el = ele;
        this.template = ele.data('templateId');
        this.render(data, append);
    }

    render(data, append) {
        const source = $(`#${this.template}`).html();
        const template = Handlebars.compile(source);
        const html = template(data);

        if (append) {
            this.$el.append(html);
        } else {
            this.$el.html(html);
        }
    }

    helpers() {
        Handlebars.registerHelper("in", function (value1, value2, options) {
            return value1.indexOf(value2) > -1 ? options.fn(this) : options.inverse(this);
        });

        Handlebars.registerHelper("math", (lvalue, operator, rvalue) => {
            const lvalueVar = parseFloat(lvalue);
            const rvalueVar = parseFloat(rvalue);

            return {
                "+": lvalueVar + rvalueVar,
                "-": lvalueVar - rvalueVar,
                "*": lvalueVar * rvalueVar,
                "/": lvalueVar / rvalueVar,
                "%": lvalueVar % rvalueVar
            }[operator];
        });

        Handlebars.registerHelper("formatDate", (datetime, format) => moment(datetime).format(format));

        Handlebars.registerHelper("ifEquals", (arg1, arg2, options) => {
            const result = arg1 === arg2 ? options.fn(this) : options.inverse(this);
            return result;
        });

        Handlebars.registerHelper("formatToInt", number => parseInt(number, 10));

        Handlebars.registerHelper("ifCond", function (v1, o1, v2, mainOperator, v3, o2, v4, options) {
            var isTrue = false,
                res = false,
                operators = {
                    "==": function (a, b) {
                        return a == b;
                    },
                    "===": function (a, b) {
                        return a === b;
                    },
                    "!=": function (a, b) {
                        return a != b;
                    },
                    "!==": function (a, b) {
                        return a !== b;
                    },
                    "<": function (a, b) {
                        return a < b;
                    },
                    "<=": function (a, b) {
                        return a <= b;
                    },
                    ">": function (a, b) {
                        return a > b;
                    },
                    ">=": function (a, b) {
                        return a >= b;
                    },
                    "&&": function (a, b) {
                        return a && b;
                    },
                    "||": function (a, b) {
                        return a || b;
                    }
                };
            var a1 = operators[o1](v1, v2);

            if (o2) {
                var a2 = operators[o2](v3, v4);
                isTrue = operators[mainOperator](a1, a2);
                res = isTrue ? options.fn(this) : options.inverse(this);
            } else {
                isTrue = a1;
                res = isTrue ? mainOperator.fn(this) : mainOperator.inverse(this);
            }

            return res;
        });

        Handlebars.registerHelper("inc", function (number, options) {
            if (typeof number === "undefined" || number === null) return null;

            // Increment by inc parameter if it exists or just by one
            return number + (options.hash.inc || 1);
        });

        Handlebars.registerHelper("toJSON", function (object) {
            return new Handlebars.SafeString(JSON.stringify(object));
        });

        Handlebars.registerHelper("subStringEllipsis", function (ellipsis, str, startIndex, endIndex) {
            if (str === "undefined" || str === null) return '';
            var sIndx = startIndex ? startIndex : 0,
                eIndx = endIndex ? endIndex : str.length,
                result = str.substring(sIndx, eIndx);

            if (ellipsis) {
                result = (str.length > endIndex) ? (result + '...') : result;
            }

            return result;
        });
    }
}
