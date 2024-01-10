import Countdown from 'countdown-js';
import $ from 'jquery';
import AEM from '../../../../core/js/AemComponent';

class EventItem extends AEM.Component {

    constructor(element) {
        super(element);

        this.name = 'Event Item';
        this.compName = this.constructor.name;
        this.digitalDataTrackable = true;
        this.hasSingleCampaign = true;

        this.campaign1 = null;
        this.title = null;
        this.$titleComponent = null;

        this.$counterNode = null;
        this.$daysNode = null;
        this.$minutesNode = null;
        this.$hoursNode = null;
        this.$secondsNode = null;

        this.selectors = {
            counter: '.eventitem__counter',
            days: '.eventitem__counter-stats-days',
            hours: '.eventitem__counter-stats-hours',
            minutes: '.eventitem__counter-stats-minutes',
            seconds: '.eventitem__counter-stats-seconds',
            stats: '.eventitem__counter-stats',
        };

        this.states = {
            hasCounter: 'eventitem__container--has-counter',
        };
    }

    init() {
        this.$counterNode = $(this.element).find(this.selectors.counter);
        this.$daysNode = $(this.element).find(this.selectors.days);
        this.$minutesNode = $(this.element).find(this.selectors.minutes);
        this.$hoursNode = $(this.element).find(this.selectors.hours);
        this.$secondsNode = $(this.element).find(this.selectors.seconds);
        this.$statsNode = $(this.element).find(this.selectors.stats);

        if (this.element.hasAttribute('data-attrib-campaign')) {
            this.campaign1 = this.element.getAttributeNode('data-attrib-campaign').value;
        }

        this.$titleComponent = $('h2', this.element);
        this.title = this.$titleComponent != null ? this.$titleComponent.text().trim() : '';

        const counterEnabled = this.$counterNode.data('counterEnabled');

        // Return if counter disabled
        if (counterEnabled === undefined) {
            return;
        }

        const eventDate = this.$counterNode.data('date');
        const eventTime = this.$counterNode.data('time');
        let eventTimezone = this.$counterNode.data('timezone');

        if (eventTimezone === 'GMT') {
            eventTimezone = '+00:00';
        }

        // remove the preceding GMT
        eventTimezone = eventTimezone.replace(/[A-Z]/g, '');

        // if we have a timzone format like +3:00, we change it to +03:00
        if (eventTimezone.length === 5) {
            const plusOrMinus = eventTimezone.substring(0, 1);
            eventTimezone = eventTimezone.substring(1).padStart(5, 0);
            eventTimezone = `${plusOrMinus}${eventTimezone}`;
        }

        const eventDateTimeZone = new Date(`${eventDate}T${eventTime}${eventTimezone}`);

        // Init actual counter
        Countdown.timer((eventDateTimeZone.getTime()), (timeLeft) => {
            this.element.classList.add(this.states.hasCounter);
            this.$statsNode.get(0).setAttribute('style', 'display: flex;');
            this.$counterNode.css('display', 'flex');
            this.$daysNode.html(timeLeft.days);
            this.$hoursNode.html(timeLeft.hours);
            this.$minutesNode.html(timeLeft.minutes);
            this.$secondsNode.html(timeLeft.seconds);
        }, () => {
            this.element.classList.remove(this.states.hasCounter);
            this.$statsNode.get(0).setAttribute('style', 'display: none;');
        });
    }
}

AEM.registerComponent('.eventitem', EventItem);
