# Farming Contract Bank Seed

A RuneLite plugin that adds a Farming contract section to the top of the bank when you still need to plant your active contract. The section shows the banked seed and, for tree contracts, the corresponding banked sapling. The normal bank ordering remains unchanged below it.

The section is hidden while the contract crop is growing, fully grown, or diseased. Empty, dead, occupied-by-another-crop, and unknown patch states show the section.

Configure a crop for each contract-eligible Farming Guild patch (including separate north and south allotments) to add a **Preplant seeds/saplings** section below the contract section. Each dropdown is limited to crops supported by Farming Guild contracts and defaults to None. Preplant seeds/saplings appear only while their corresponding patch is unplanted or dead, and only when the item is banked and visible in the current bank tab; duplicates already in the contract section appear there only. The preplant section also works without an active contract. Spirit tree, anima, and Hespori patches are not included.

The plugin uses RuneLite's Time Tracking plugin for the active contract and the Farming Guild patch varbits for preplant availability. It changes only the bank's visual layout; it does not move items between bank slots.

## Development

```sh
./gradlew build
```

## License

BSD 2-Clause License. See [LICENSE](LICENSE).
