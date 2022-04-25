import { useEffect, useState } from "react";
import axios from "axios";

export default function useRequest() {
	const [res, setRes] = useState(null);
	const [error, setError] = useState(null);
	const [loading, setLoading] = useState(false);
	const header = { "Content-Type": "application/json" };

	async function request(url, method, data) {
		setLoading(true);
		await axios({
			method: method,
			url: url,
			data,
			headers: header,
		})
			.then((resp) => {
				setRes(resp.data);
			})
			.catch((err) => {
				setError(err);
			})
			.finally(() => {
				setLoading(false);
			});
	}

	return [res, error, loading, request];
}
