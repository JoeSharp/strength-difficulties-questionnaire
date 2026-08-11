pub struct IncrementingIndex {
    index: usize,
}

impl IncrementingIndex {
    pub fn create() -> IncrementingIndex {
        IncrementingIndex { index: 0 }
    }

    pub fn next_index(&mut self) -> usize {
        self.index += 1;
        self.index
    }
}
