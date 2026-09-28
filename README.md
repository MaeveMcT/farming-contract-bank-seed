# Farming Contract Bank Seed

A RuneLite plugin that adds a Farming contract section to the top of the bank when you still need to plant your active contract. The section shows the banked seed and, for tree contracts, the corresponding banked sapling. The normal bank ordering remains unchanged below it.

The section is hidden while the contract crop is growing, fully grown, or diseased. Empty, dead, occupied-by-another-crop, and unknown patch states show the section.

Configure a crop for each contract-eligible Farming Guild patch (including separate north and south allotments) to add a **Preplant seeds/saplings** section below the contract section. Each dropdown is limited to crops supported by Farming Guild contracts and defaults to None. Preplant seeds/saplings appear only while their corresponding patch is unplanted, dead, or has already had its health checked, and only when the item is banked and visible in the current bank tab; duplicates already in the contract section appear there only. The preplant section also works without an active contract. Spirit tree, anima, and Hespori patches are not included.

Optionally choose compost, supercompost, ultracompost, or a bottomless compost bucket (filled or empty). The default is None. When banked and visible in the current bank tab, compost joins the contract section while contract seeds or saplings are shown; otherwise it appears in the preplant section, even after the seeds and saplings have been withdrawn. It is shown only when a contract needs planting or a configured preplant patch is ready.

This only changes how items are displayed in the bank; it does not move them between slots.

## License

BSD 2-Clause License. See [LICENSE](LICENSE).
