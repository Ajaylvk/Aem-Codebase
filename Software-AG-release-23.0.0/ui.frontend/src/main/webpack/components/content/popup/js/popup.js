import $ from 'jquery';
import PubSub from '../../../../core/js/utils/pubsub';

(() => {
    $(document).on('click', '.sag-popup__open', (e) => {
        const currTarget = e.currentTarget;
        const popupId = $(currTarget).data('popup-id');

        // say popup is opened
        PubSub.publish('sag-popup-open', { popupId });

        $(`#${popupId}`).removeClass('hide');
        $('html, body').addClass('sag-popup--no-scroll');
    });

    $(document).on('click', '.sag-popup__grey-area', (e) => {
        const currTarget = e.currentTarget;
        const popupId = $(currTarget).data('popup-id');

        // say popup is opened
        PubSub.publish('sag-popup-open', { popupId, greyBg: true });

        $(currTarget).closest('.sag-popup').addClass('hide');
        $('html, body').removeClass('sag-popup--no-scroll');
    });

    $(document).on('click', '.sag-popup__cross-btn, .sag-popup__close-btn', (e) => {
        const currTarget = e.currentTarget;
        const popupId = $(currTarget).data('popup-id');

        // say popup is opened
        PubSub.publish('sag-popup-open', { popupId, closeBtn: true });

        $(currTarget).closest('.sag-popup').addClass('hide');
        $('html, body').removeClass('sag-popup--no-scroll');
    });
})();
