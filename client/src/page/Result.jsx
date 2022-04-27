import React, { useState, useEffect, useMemo } from "react";
import { InputDate } from "../component/InputDate";
import { Chart } from "../component/Chart";
import { InputCompanyName } from "../component/InputCompanyName";
import { SubmitButton } from "../component/SubmitButton";
import { InitButton } from "../component/InitButton";
import useRequest from "../hooks/useRequest";
import urls from "../config/api";
import { buildData } from "../utils/buildData";
import { ReloadButton } from "../component/ReloadButton";
import { InputScaler } from "../component/InputScaler";

function ResultPage() {
	const [moments, setMoments] = useState();
	const [companyName, setCompanyName] = useState();
	const [companyNames, setCompanyNames] = useState();
	const [timePeriod, setTimePeriod] = useState({
		start: "2012-1-1",
		to: "2022-1-1",
	});
	const [offset, setOffset] = useState(0);
	const [limit, setLimit] = useState(20);
	const [res, error, loading, request] = useRequest(
		urls.selectByTimeAndComp,
		"POST"
	);
	const [data, setData] = useState([]);

	const data_dates = useMemo(() => {
		const dates = moments?.map((m) => m.format("yyyy-MM-DD"));
		if (dates) return { start: dates[0], end: dates[1] };
	}, [moments]);

	const onClickFun = () => {
		request({
			start: data_dates.start,
			end: data_dates.end,
			name: companyName,
			offset: offset,
			limit: limit,
		});
	};

	useEffect(() => {
		if (res) {
			const d = res.Data;
			if (d) {
				// console.log(buildData(d));
				setData(buildData(d));
			}
		}
	}, [res]);

	useEffect(() => {
		if (error) console.error(error);
	}, [error]);

	useEffect(() => {}, [companyNames, timePeriod]);

	return (
		<>
			<InitButton />
			<Chart data={data} />
			<InputDate
				start={timePeriod.start}
				to={timePeriod.to}
				setMoments={setMoments}
			/>
			<InputCompanyName
				companyNames={companyNames}
				setCompanyName={setCompanyName}
			/>
			<InputScaler
				setLimit={setLimit}
				setOffset={setOffset}
				defaultOffset={0}
				defaultLimit={10}
				offsetRange={[0, 50]}
				limitRange={[0, 30]}
			/>
			<SubmitButton onClick={onClickFun} loading={loading}>
				Submit
			</SubmitButton>

			<ReloadButton
				setCompanyNames={setCompanyNames}
				setTimePeriod={setTimePeriod}
				setDataEmpty={() => setData([])}
			/>
		</>
	);
}

export default ResultPage;
