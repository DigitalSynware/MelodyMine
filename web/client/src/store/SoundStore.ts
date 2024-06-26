import {createWithEqualityFn} from "zustand/traditional";
import {shallow} from "zustand/shallow";
import {Howl} from "howler";
import sounds from "@/sounds";
import {ISoundSettings} from "@/interfaces";

export interface ISound {
    name: string
    howl: Howl
    loop?: boolean
    volume?: number
}

interface State {
    soundList: ISound[]
    soundSettings: ISoundSettings
}

interface Actions {
    initSounds: () => void
    setSoundSettings: (soundSettings: ISoundSettings) => void
}

export const useSoundStore = createWithEqualityFn<State & Actions>((setState) => ({
    soundSettings: {
        sound3D: true,
        lazyHear: true,
        maxDistance: 30,
        refDistance: 5,
        innerAngle: 120,
        outerAngle: 180,
        outerVolume: 0.3

    },
    soundList: [],
    initSounds: () => {
        const tempSounds: ISound[] = []
        sounds.forEach(sound => {
            tempSounds.push({
                name: sound.name,
                howl: new Howl({src: sound.url, volume: sound.volume, loop: sound.loop, html5: true, format: ["mp3"]})
            })
        })
        tempSounds.forEach(sound => sound.howl.load())
        setState(() => ({soundList: tempSounds}))
    },
    setSoundSettings: (soundSettings) => setState(() => ({soundSettings}))


}), shallow)

