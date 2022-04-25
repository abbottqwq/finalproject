import React, { useState, useEffect, useMemo } from "react";
import { InputDate } from "../component/InputDate";
import { Chart } from "../component/Chart";
import { InputCompanyName } from "../component/InputCompanyName";
import { SubmitButton } from "../component/SubmitButton";
import { InitButton } from "../component/InitButton";
import useRequest from "../hooks/useRequest";
import urls from "../config/api";
import { buildData } from "../utils/buildData";

function ResultPage() {
	const [moments, setMoments] = useState();
	const [companyName, setCompanyName] = useState();
	const [res, error, loading, request] = useRequest();
	const [data, setData] = useState([]);
	const data_dates = useMemo(() => {
		const dates = moments?.map((m) => m.format("yyyy-MM-DD"));
		if (dates) return { start: dates[0], end: dates[1] };
	}, [moments]);

	const onClickFun = () => {
		request(urls.selectByTime, "POST", data_dates);
	};

	useEffect(() => {
		if (res) {
			const d = res.Data;
			if (d) {
				console.log(buildData(d));
				setData(buildData(d));
			}
		}
	}, [res]);
	useEffect(() => {
		console.log(error);
	}, [error]);
	return (
		<div>
			<Chart data={data} />
			<InputDate setMoments={setMoments} />
			<InputCompanyName setCompanyName={setCompanyName} />
			<SubmitButton onClick={onClickFun} loading={loading} />
			<InitButton />
		</div>
	);
}

export default ResultPage;
