import $ from 'jquery';
import AEM from '../../../../core/js/AemComponent';

class Table extends AEM.Component {

    constructor(element) {
        super(element);
        this.compName = this.constructor.name;
        this.name = 'Table';
        this.digitalDataTrackable = true;

        this.title = null;
        this.$titleComponent = null;

        this.selectors = {
            component: '.table',
            headCells: '.styled-table thead .tableitem__text',
            bodyCells: '.styled-table tbody td',

        };

        this.cssClasses = {
            selected: 'selected',
        };
        this.headerValues = [];
    }

    init() {
        const self = this;
        this.$headCells = $(this.element).find(this.selectors.headCells);
        this.$bodyCells = $(this.element).find(this.selectors.bodyCells);

        this.$titleComponent = $('h2', this.element);
        this.title = this.$titleComponent != null ? this.$titleComponent.text().trim() : '';

        // Collect header td values
        $.each(this.$headCells, (index, value) => {
            self.headerValues.push($.trim($(value).text()));
        });

        // Distribute header td cell values onto body td cells for Mobile view
        $.each(this.$bodyCells, (index, node) => {
            const $parent = $(node).parent('tr');
            const tdIndex = $parent.find('td').index(node);
            $(node).attr('data-title', self.headerValues[tdIndex]);
        });
    }
}

AEM.registerComponent('.table', Table);
