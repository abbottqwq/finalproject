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
	const [wordData, setWordData] = useState([]);
	const [compData, setCompData] = useState([]);
	const onClickFun = () => {
		request({ limit: limit, offset: offset });
	};

	const [res_comp, error_comp, loading_comp, request_comp] = useRequest(
		urls.selectCompanyName,
		"POST"
	);
	useEffect(() => {
		if (res_comp) {
			const c = res_comp.Data;
			console.log(c);
			// const words = d?.map((t) => {
			// 	return { text: t.tweets, value: parseInt(t.freq) };
			// });
			// setWordData(words);
			if (c) {
				const comp = c?.map((t) => {
					return { text: t.author_id, value: parseInt(t.freq) };
				});
				setCompData(comp);
			}
			// console.log(compData);
		}
	}, [res_comp]);

	// useEffect(() => {
	// 	request_comp();
	// }, []);

	const onClick_comp = () => {
		request_comp();
	};

	const req = (limit, offset) => {
		request({ limit: limit, offset: offset });
	};

	// const onInit = () => {};

	useEffect(() => {
		req(limit, offset);
	}, [limit, offset]);

	useEffect(() => {
		if (res) {
			// console.log("res changed");
			const d = res.Data;

			if (d) {
				// const sum = _.sumBy(d, (o) => parseInt(o.freq));
				// console.log(sum);
				const words = d?.map((t) => {
					return { text: t.tweets, value: parseInt(t.freq) };
				});
				setWordData(words);
			}
		}
	}, [res]);

	useEffect(() => {
		if (error) console.error(error);
	}, [error]);

	useEffect(() => {
		if (error_comp) console.error(error_comp);
	}, [error_comp]);

	return (
		<>
			<WordCloud data={wordData} />
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
			<WordCloud data={compData} loading={loading_comp} />
			<SubmitButton loading={loading_comp} onClick={onClick_comp}>
				get
			</SubmitButton>
		</>
	);
}
