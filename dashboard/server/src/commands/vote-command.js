const MEDIA_TYPE = [
  "IOS",
  "ANDROID",
  "SMS",
  "FB",
  "TWITTER"
];

const PROFILE_IMG = [
  "https://pbs.twimg.com/profile_images/738437941962412032/x6eL42WZ_bigger.jpg",
  "https://pbs.twimg.com/profile_images/2717434746/56ddc77ba7ee208526866247108b71eb_400x400.png",
  "https://pbs.twimg.com/media/CvDkBqwWgAA4lWh.jpg",
  "https://pbs.twimg.com/media/Cvms3NOWAAAodef.jpg",
  "https://pbs.twimg.com/media/CvpMxmyWEAAi80u.jpg"
];

let voteInterval;

export function startVoteSimulation(fn) {
  let msgIndex = 1;
  const sendNextVote = () => {
    const mediaType = MEDIA_TYPE[msgIndex % MEDIA_TYPE.length];

    fn({
      type: "VOTE_TRAIN",
      payload: {
        trainId: Math.round(Math.random() % 2) + 1,
        media: mediaType,
        count: 1,
        avatar: (mediaType === 'FB' || mediaType === 'TWITTER')  ? PROFILE_IMG[Math.round(Math.random()* PROFILE_IMG.length) % PROFILE_IMG.length] : undefined
      }
    });

    msgIndex++;
    if (msgIndex > 100) {
      stopVoteSimulation();
    }
  };

  if (voteInterval === undefined) {
    console.log('START_VOTE_SIMULATION');
    voteInterval = setInterval(sendNextVote, 800);
  } else {
    console.log('Already started VOTE_SIMULATION');
  }
}

export function stopVoteSimulation() {
  if (voteInterval) {
    console.log('STOP_VOTE_SIMULATION');
    clearInterval(voteInterval);
    voteInterval = undefined;
  }
}


