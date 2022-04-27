import React, {
	useState,
	useEffect,
	useMemo,
	useCallback,
	useRef,
} from "react";
import { SubmitButton } from "../component/SubmitButton";
import { InitButton } from "../component/InitButton";
import useRequest from "../hooks/useRequest";
import urls from "../config/api";
import { WordCloud } from "../component/WordCloud";
import { InputScaler } from "../component/InputScaler";
import _ from "lodash";

export default function WordCloudPage() {
	// const limit = 50;
	// const offset = 0;
	const [limit, setLimit] = useState(50);
	const [offset, setOffset] = useState(0);
	const [res, error, loading, request] = useRequest(urls.selectAll, "POST");
	const [data, setData] = useState([]);
	const onClickFun = () => {
		request({ limit: limit, offset: offset });
	};

	const req = (limit, offset) => {
		request({ limit: limit, offset: offset });
	};

	useEffect(() => {
		req(limit, offset);
	}, [limit, offset]);

	useEffect(() => {
		if (res) {
			console.log("res changed");
			const d = res.Data;
			if (d) {
				const words = d?.map((t) => {
					return { text: t.tweets, value: parseInt(t.freq) };
				});
				setData(words);
			}
		}
	}, [res]);

	useEffect(() => {
		if (error) console.error(error);
	}, [error]);

	return (
		<>
			<WordCloud data={data} />
			<InputScaler
				setLimit={setLimit}
				setOffset={setOffset}
				defaultLimit={100}
				defaultOffset={0}
				offsetRange={[0, 100]}
				limitRange={[10, 100]}
				wait={500}
			/>
			<SubmitButton loading={loading} onClick={onClickFun}>
				get
			</SubmitButton>
			<br />
		</>
	);
}
