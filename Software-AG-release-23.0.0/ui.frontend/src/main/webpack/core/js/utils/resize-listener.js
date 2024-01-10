import $ from 'jquery';

/**
 * Special listener for resize events
 */
class ResizeListener {
    /**
     *
     */
    constructor() {
        const $window = $(window);

        /**
         * @private
         * @type {number}
         */
        this.previousWidth = $window.width();


        $window
            .on('resize', () => this.onResize($window));
    }


    /**
     * @private
     * @param {jQuery} $window
     */
    onResize($window) {
        const width = $window.width();

        if (width !== this.previousWidth) {
            this.previousWidth = width;
            $(this).trigger('resize.horizontal');
        }
    }
}

export default new ResizeListener();
