/* eslint-disable */
class PubSub {
    constructor() {
        this.subIds = 0;
        this.subscriptions = {};
    }

    subscribe(topic, fn) {
        if (!this.subscriptions[topic]) this.subscriptions[topic] = {};
        const token = ++this.subIds;
    // Validate topic name and function constructor
        this.subscriptions[topic][token] = fn;

        return () => this.unsubscribe(topic, token);
    }

    publish(topic, ...args) {
      setTimeout(() => {
        const subs = this.subscriptions[topic];
        if (!subs) return false;
        Object.values(subs).forEach(sub => sub(...args));
      },0);
    }

    unsubscribe(topic, token) {
        if (!token) delete this.subscriptions[topic]; // Delete all subscriptions for the topic
        this.subscriptions[topic] && (delete this.subscriptions[topic][token]); // Delete specific subscription
    }
}

export default new PubSub();
