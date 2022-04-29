import React, { useState, useEffect, useMemo } from "react";
import { Button } from "antd";
import { ReloadOutlined } from "@ant-design/icons";
import useRequest from "../hooks/useRequest";
import urls from "../config/api";

export function ReloadButton({
	setCompanyNames,
	setTimePeriod,
	setDataEmpty,
	isGetOnLoad = true,
}) {
	const [res_Comp, error_Comp, loading_Comp, request_Comp] = useRequest(
		urls.selectCompanyName,
		"POST"
	);
	const [
		res_timePeriod,
		error_timePeriod,
		loading_timePeriod,
		request_TimePeriod,
	] = useRequest(urls.selectTimePeriod, "POST");

	useEffect(() => {
		if (res_Comp) {
			// console.log(res_Comp.Data);
			setCompanyNames(
				res_Comp.Data.map(
					(c) => [{ value: c.author_id, freq: parseInt(c.freq) }][0]
				)
			);
		}
		if (res_timePeriod) {
			setTimePeriod({
				start: res_timePeriod.Start_Date,
				to: res_timePeriod.End_Date,
			});
		}
	}, [res_Comp, res_timePeriod, setCompanyNames, setTimePeriod]);

	useEffect(() => {
		if (error_Comp) console.error(error_Comp);
	}, [error_Comp]);

	useEffect(() => {
		if (error_timePeriod) console.error(error_timePeriod);
	}, [error_timePeriod]);

	const isLoading = useMemo(() => loading_Comp || loading_timePeriod, [
		loading_Comp,
		loading_timePeriod,
	]);
	const onClick = () => {
		request_Comp();
		request_TimePeriod();
		setDataEmpty();
	};

	useEffect(() => {
		if (isGetOnLoad) onClick();
	}, []);

	return (
		<Button
			type="primary"
			icon={<ReloadOutlined />}
			loading={isLoading}
			onClick={onClick}
		/>
	);
}
