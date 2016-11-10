const TWEETS = [
  "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
  "Integer ac erat convallis, molestie ex in, faucibus nunc.",
  "Donec eu tellus ullamcorper, bibendum urna eu, mattis velit.",
  "Vivamus quis purus tempor, congue est id, pellentesque ex.",
  "Cras rhoncus dolor eu massa laoreet, in tristique dui interdum.",
  "Praesent nec tortor ac odio vehicula commodo.",
  "Duis non nisl fringilla est eleifend dignissim sed bibendum tortor.",
  "Integer a risus quis lacus pharetra fermentum.",
  "Cras finibus dolor et nulla feugiat, et tempor nunc feugiat.",
  "Mauris molestie elit vel neque pellentesque, aliquet condimentum ex vulputate.",
  "Vestibulum faucibus metus nec eros gravida, vel pulvinar nunc viverra.",
  "Suspendisse placerat ex a orci mattis, eget suscipit mauris porta.",
  "Phasellus facilisis tortor luctus posuere sagittis.",
  "Nunc quis mauris sit amet leo vulputate lacinia eu at eros."
];

let tweetsInterval;

export function startTwitterSimulation(fn) {
  let msgIndex = 1;
  const sendNextTweet = () => {
    const tweetMessage = TWEETS[msgIndex % TWEETS.length];

    const r = Math.round(Math.random() * 3);

    fn({
      type: "TWITTER_TIMELINE",
      payload: {
        tweetId: new Date().getTime(),
        username: "abachar",
        avatarUrl: "http://loremflickr.com/100/100/portrait",
        //imageUrl: r == 1 ? "http://lorempixel.com/640/360/technics/M-" + msgIndex : undefined,
        imageUrl: r == 1 ? "https://unsplash.it/640/360/?image=" + msgIndex: undefined,
        message: tweetMessage,
        tweetedAt: 1473606653715
      }
    });

    msgIndex++;
    if (msgIndex > 50) {
      stopTwitterSimulation();
    }
  };

  if (tweetsInterval === undefined) {
    console.log('START_TWITTER_SIMULATION');
    tweetsInterval = setInterval(sendNextTweet, 1000);
  } else {
    console.log('Already started');
  }
}

export function stopTwitterSimulation() {
  if (tweetsInterval) {
    console.log('STOP_TWITTER_SIMULATION');
    clearInterval(tweetsInterval);
    tweetsInterval = undefined;
  }
}


