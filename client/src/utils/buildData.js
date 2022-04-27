export const buildData = (data) => {
	const sum = data.reduce((p, t) => p + parseInt(t.freq), 0);
	// console.log(sum);
	return data.map((d) => {
		// angle: d.freq, label: d.tweets
		return { x: d.tweets, y: d.freq, p: d.freq / sum };
	});
};
