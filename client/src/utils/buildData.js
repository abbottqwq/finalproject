export const buildData = (data) => {
	return data.map((d) => {
		return { angle: d.freq, label: d.tweets };
	});
};
