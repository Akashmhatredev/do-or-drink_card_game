<template>
  <q-page class="tw-bg-black tw-font-mono">
    <div class="tw-flex tw-pb-[120px] tw-justify-center tw-items-center tw-h-screen">
      <!-- Home Page -->
      <div class=" tw-text-white" v-if="page == 'home_page'">
        <div class="tw-text-[40px]"> Do or Drink </div>
        <div class="tw-text-center tw-text-[25px] tw-mt-24">
          <div @click="second_step('first_base')" class="tw-my-4">First Base</div>
          <div @click="second_step('second_base')" class="tw-my-4">Second Base</div>
          <div @click="second_step('third_base')" class="tw-my-4">Third Base</div>
          <div @click="to_custom_page()" class="tw-my-4">Custom</div>
        </div>
      </div>
      <!-- Dare Page -->
      <div v-if="page == 'dare_page'" class="tw-mx-auto tw-max-w-2xl tw-px-4 sm:tw-px-6 lg:tw-max-w-7xl ">
        <div class="lg:tw-grid lg:tw-auto-rows-min lg:tw-grid-cols-12 lg:tw-gap-x-8 tw-items-center">
          <div class="card-container tw-perspective-1000 tw-w-[85vw] tw-h-[70vh] tw-mx-auto tw-cursor-pointer">
            <div @click="nextText"
              class="card tw-w-full tw-h-full tw-transform-style-preserve-3d tw-transition-transform tw-duration-1000 tw-ease-in-out"
              :style="{ transform: `rotateY(${rotationAngle}deg)` }">
              <div
                class="card-face tw-px-4 tw-backface-hidden tw-text-center tw-absolute tw-top-0 tw-left-0 tw-w-full tw-h-full tw-flex tw-items-center tw-justify-center tw-text-lg tw-font-bold tw-bg-gray-100 tw-border tw-border-gray-300 tw-rounded-lg tw-shadow-md">
                {{ currentText }}
              </div>
            </div>
            <div class="tw-flex tw-mt-10 tw-justify-around tw-w-[85vw]">
              <q-btn @click="to_main_menu()" flat round color="white" size="25px" icon="logout" />
              <q-btn :disable="currentIndex == 0" @click="previousText()" flat round color="white" size="25px"
                icon="undo" />
              <q-btn @click="textToAudio(currentText)" flat round color="white" size="25px" icon="volume_up" />
            </div>
          </div>
        </div>
      </div>
       <!-- Custom Page -->
      <div class=" tw-text-white tw-text-center" v-if="page == 'custom_page'">
        <div class="tw-absolute tw-top-4 tw-left-4">
          <q-btn flat color="white" label="Back" @click="page = 'home_page'" />
        </div>
        <div class="tw-text-[30px]">Custom Dare Section</div>
         <!-- Home Input Page -->
        <div v-if="custom_section_page === 'input_page'" class="tw-text-[18px] tw-mt-24">
          <div class="tw-flex tw-justify-between tw-my-8">
            <p class="tw-mt-2">No. of Player</p>
            <q-input dark dense v-model.number="numberOfPlayers" type="number" outlined style="max-width: 50px;" />
          </div>
          <div class="tw-flex tw-justify-between  tw-my-8">
            <p class="tw-mt-2">No. of Dares per player</p>
            <q-input dark dense v-model.number="numberOfDaresPerPlayer" type="number" outlined
              style="max-width: 50px;" />
          </div>
          <div class="tw-mt-24">
            <q-btn @click="to_dare_step()" :disable="numberOfPlayers === 0 || numberOfDaresPerPlayer === 0" flat
              color="white" size="20px" label="Next" />
          </div>
        </div>
        <!-- Dare Input Page -->
        <div v-if="custom_section_page === 'dare_page'" class="tw-text-[18px] tw-mt-24">
          <div class="tw-p-4">
            <h3>Player {{ currentPlayer }}'s Turn</h3>
            <p>Enter a dare below ({{ dareCount }}/{{ numberOfDaresPerPlayer }})</p>

            <q-input v-model="currentDare" dark outlined dense placeholder="Enter a dare" class="tw-my-8" />
            <q-btn label="Add Dare" flat color="white" @click="submitDare"
              :disable="!currentDare || dareCount >= numberOfDaresPerPlayer" class="tw-mt-2" />
            <q-btn label="Next Player" flat color="white" @click="nextPlayer"
              :disable="currentPlayer >= numberOfPlayers || dareCount < numberOfDaresPerPlayer" class="tw-mt-2" />

            <q-btn label="Submit Dares" flat color="white" @click="submitAllDares"
              :disable="currentPlayer < numberOfPlayers || dareCount < numberOfDaresPerPlayer" class="tw-mt-2" />

            <div v-if="currentPlayerDares.length > 0" class="tw-mt-6 tw-text-white">
              <h4>Your Dares:</h4>
              <ul>
                <li v-for="(dare, index) in currentPlayerDares" :key="index" class="tw-flex tw-justify-between tw-text-[10px] tw-items-center tw-gap-2">
                  <div>{{ dare }}</div>
                  <q-btn icon="delete" size="sm" color="white" flat dense @click="deleteDare(index)" />
                </li>
              </ul>
            </div>
          </div>
        </div>
      </div>
    </div>
  </q-page>
</template>

<script>
import { TextToSpeech } from '@capacitor-community/text-to-speech';
export default {
  data() {
    return {
      currentPlayer: 1,
      currentDare: "",
      currentPlayerDares: [],
      allDares: [],
      numberOfPlayers: 0,
      numberOfDaresPerPlayer: 0,
      custom_section_page: "input_page",
      page: 'home_page',
      texts: [],
      currentIndex: 0,
      rotationAngle: 0, // Tracks the accumulated rotation angle
      first_base: ["List all of your ex’s in alphabetical order.",

        "Give your best impersonation of the opposite sex.",

        "Let the person on your right draw on your face with a permanent marker.",

        "Sing your ABCs while twerking.",

        "Stand up and do jumping jacking until your next turn.",

        "Go on Facebook Live and read the back of a shampoo bottle in a British accent.",

        "Go outside and try to summon the rain.",

        "Trade clothes with the person on your left.",

        "Sing a song chosen by the group while eating spoonfuls of peanut butter",

        "Hold hands with the person to your left for the duration of the game. (If you or the other person let go at any point, you each have to do 2 shots each).",

        "Hand your phone to the person across from you and let them post whatever they want to your social media accounts.",

        "Talk to your hand like you’re making a game plan to sleep with someone else in the group.",

        "Send a random gif to the first person in your contact list with no explanation.",

        "The person across from you gets to choose your Facebook profile picture for the whole evening.",

        "Do your best version of the “Single Ladies” dance and record it.",

        "The person on your right gets to go on your online dating profile for 3 minutes to do whatever they want.",

        "Blindfold yourself and take a shot of the group’s choosing.",

        "Try to hit on someone in the group like you’re at a bar. You have 3 minutes.",

        "Open Instagram and choose a person at random (cannot be someone in the group), and like every single post from the past year.",

        "Leave a s*xy voicemail to your ex.",

        "Shotgun a beer in under 15 seconds.",

        "Use provocative hand gestures every time you talk for the next two rounds.",

        "Perform the most flexible thing you can do with your body.",

        "Scroll through your contacts until someone says stop. Either call that person or delete them.",

        "Tell the group two truths and a lie, they have to guess which one is the lie.",

        "Put 10 different liquids into a cup and drink it.",

        "Make a video of you singing your favorite “love-making” song and post it on your Snapchat story.",

        "Give a detailed presentation on how you “woo” a lover in the style of a Ted Talk.",

        "Do your best “sexy crawl”.",

        "Pick up a random book and read it in the most seductive voice manageable.",

        "For one minute, call a random number on your phone and tell the person how much you want to make love to them.",

        "Call a hospital and ask for emergency condoms.",

        "Feed someone almonds, candy, etc… using just your mouth."],

      second_base: ['Have each person in the group take turns feeding you candy, etc… and you have to say, “Thank you, Daddy,” after each one.',

        'Twerk on someone to the song Sexy Can I for 2 minutes.',

        'Remove one piece of clothing every time you get a non-text notification on your phone.',

        'Take a shot off of the person’s body to your right.',

        'Demonstrate “how-to” finger a woman.',

        'Lick another person’s lips. They have to resist kissing back. If they kiss back, they take a shot.',

        'Put a timer on for 15 seconds and act out as many sexual positions as you can, clothes on.',

        'Describe yourself only using dirty talk.',

        'Do your best fake “O” while looking the person to the left of you in the eye.',

        'Call the most recent person on your call log and make moaning noises.',

        'Walk down the street in your underwear.',

        'Have the person to your left do 10 squats while you lie underneath them.',

        'Have someone go on Amazon and buy you a “special toy” for $20 or less.',

        'Tough tongues with the person across from you.',

        'Demonstrate to the room how to put a condom on using a banana.',

        'Play the rest of the game in just your underwear.',

        'Give the person to your right a hickey.',

        'Like whipped cream off of a body part belonging to the youngest member of the group.',

        'Wear a blindfold and kiss whatever body part is put in front of you.',

        'Take a seductive selfie and send it to someone in the room.',

        'Close your eyes and swipe right on your dating app 10 times.',

        'Do a reenactment of the first time you had sex.',

        'Take off your underwear and give them to a cute stranger.',

        'Give a lap dance to someone of your choice.',

        'Pretend like you’re making sweet, sweet, love to the couch for one minute.',

        'Take someone’s shirt off with your teeth.',

        'Send a shirtless selfie to someone you’ve slept with.',

        'Post a selfie of your most outrageous “O” face on your Snapchat story.',

        'Kiss the second person to your left in any five places but their lips.',

        'Find something sweet, put it in your mouth, and someone else has to take it out with their mouth.',

        'Do a pole dance performance without a pole.',

        'Have someone in the group take a “sexy” selfie of yourself and send it to the last person you texted.',

        'Change your Facebook status to “I’m coming . . . I’m coming . . .”. Then, one minute later, change it to “I just came.”'
      ],

      third_base: ['Take your pants off, put on “Pony” by Ginuwine, and vacuum seductively for the duration of the song.',

        'Kiss your three favorite body parts on the person of your choosing.',

        'Demonstrate your best oral sex move on the nearest appropriate object.',

        'Go around the room and guess everyone’s kinks.',

        'Kiss the neck of the person to your left for thirty seconds.',

        'Undress down to your underwear and any accessories you have on. Take a shot for each piece of clothing you want to keep on.',

        'Pull up your shirt and play with your nipples until they’re hard.',

        'Perform a striptease to an ‘80’s hit.',

        'Try to arouse someone without touching any sensual places.',

        'Put a hickey on the inner thighs of someone in the group.',

        'Hold an ice cube between your teeth and trace it along the chest of the person to your right.',

        'Lightly trace your hands over someone else’s lips and whisper, “I’m coming, I’m coming,” five times.',

        'Let someone tie you up to a chair and give you a lapdance.',

        'Face the person to your left, and rub your hand in their underwear for a whole minute.',

        'Wearing just your underwear, hop in the shower with someone of your choice for 60 seconds.',

        'Look at someone and touch yourself as you’d touch them.',

        'Offer your rear to another person for spanking.',

        'Take off someone’s bra without removing their shirt.',

        'The person to your right is asked what their favorite role play is, and you get to act like whatever character they choose.',

        'Rub peanut butter on a body part of the person to your left then lick it off.',

        'One player puts a shot cup in their mouth; you have to collect it and drink everything without using your hand.',

        'Pick four people in the room, kiss one, feel one out, smack one, and give the last person a lap dance.',

        'Bend over the oldest players’ knee, and have them spank you twenty times while you say. “I’m sorry, I’ve been naughty Daddy.” ',

        'Dry hump another player to the song Lollipop.',

        'Do your best impression of a porno that involves a pizza delivery guy.',

        'Lick another player’s nipples for 15 seconds.',

        'Choose a partner and perform your favorite sec position.',

        'Motorboat the person to your right for 10 seconds.',

        'Tell the group when you last orgasmed and describe in detail how you orgasmed.',

        'Take someone into the next room for “7 minutes in heaven”.',

        'Do a naked somersault.',

        'You have to leave an R-rated voicemail for an ex.',

        'The person to your right has to ride you like a cowboy and you have to buck like a bronco.',

        'Give a cooking demonstration of “How to Make Cereal” completely in the buff.']
    };
  },
  computed: {
    dareCount() {
      return this.currentPlayerDares.length;
    },
    currentText() {
      if (this.currentIndex >= this.texts.length) {
        return "End";
      }
      return this.texts[this.currentIndex];
    },
  },
  methods: {
    to_custom_page() {
      this.currentPlayer = 1
      this.currentDare = ""
      this.currentPlayerDares = []
      this.allDares = []
      this.numberOfPlayers = 0
      this.numberOfDaresPerPlayer = 0
      this.custom_section_page = "input_page"
      this.page = 'custom_page'
    },
    deleteDare(index) {
      this.currentPlayerDares.splice(index, 1);
    },
    submitDare() {
      if (this.currentDare && this.dareCount < this.numberOfDaresPerPlayer) {
        this.currentPlayerDares.push(this.currentDare.trim());
        this.currentDare = "";
      }
    },
    nextPlayer() {
      if (this.currentPlayer < this.numberOfPlayers && this.dareCount === this.numberOfDaresPerPlayer) {
        this.allDares.push(...this.currentPlayerDares);
        this.currentPlayer++;
        this.currentPlayerDares = [];
        this.currentDare = "";
      }
    },
    submitAllDares() {
      if (this.dareCount === this.numberOfDaresPerPlayer) {
        this.allDares.push(...this.currentPlayerDares);
        console.log("All Dares Submitted:", this.allDares);
        this.texts = this.allDares.sort(() => Math.random() - 0.5);
        this.page = 'dare_page';
      }
    },
    to_dare_step() {
      this.custom_section_page = 'dare_page'
    },
    to_main_menu() {
      this.page = 'home_page'
      this.currentIndex = 0
    },
    second_step(base) {
      const baseMapping = {
        first_base: this.first_base,
        second_base: this.second_base,
        third_base: this.third_base,
      };

      const selectedBase = baseMapping[base];

      if (selectedBase) {
        // Shuffle the selected base list
        this.texts = selectedBase.sort(() => Math.random() - 0.5);
      } else {
        this.texts = ["Something went wrong go back to main page"];
      }

      this.page = 'dare_page';
    },
    previousText() {
      if (this.currentIndex === 0) {
        return;
      }
      this.rotationAngle -= 720;
      setTimeout(() => {
        this.currentIndex--;
        if (this.currentIndex < 0) {
          this.currentIndex = 0;
        }
      }, 1000);
    },
    nextText() {
      if (this.currentText === "End") {
        return;
      }
      this.rotationAngle += 720;
      setTimeout(() => {
        this.currentIndex++;
        if (this.currentIndex >= this.texts.length) {
          this.currentIndex = this.texts.length;
        }
      }, 1000);
    },
    // textToAudio(text, lang = "en-US") {
    //   // Check if the browser supports the Web Speech API
    //   if (!("speechSynthesis" in window)) {
    //     console.error("Text-to-Speech is not supported in this browser.");
    //     return;
    //   }
    //   // Create a new SpeechSynthesisUtterance instance
    //   const utterance = new SpeechSynthesisUtterance(text);
    //   // Set the language (default is English - US)
    //   utterance.lang = lang;
    //   // Optional: Set the voice, pitch, and rate
    //   utterance.pitch = 1; // Normal pitch
    //   utterance.rate = 1;  // Normal speed
    //   // Speak the text
    //   speechSynthesis.speak(utterance);
    // }
    async textToAudio(text, lang = "en-US") {
      try {
        await TextToSpeech.speak({
          text: text,
          lang: lang,
          pitch: 1.0,
          rate: 1.0,
        });
      } catch (error) {
        console.error("Error using Text-to-Speech plugin:", error);
      }
    }
  },
};
</script>

<style scoped>
.card-container {
  perspective: 1000px;
}
</style>
