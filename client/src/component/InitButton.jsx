import React, { useState, useEffect, useMemo } from "react";
import { Button } from "antd";
import useRequest from "../hooks/useRequest";
import urls from "../config/api";

export function InitButton() {
	const [res, error, loading, request] = useRequest();
	const onClickFun = () => {
		request(urls.initData, "POST");
	};
	useEffect(() => {
		if (res) console.log(res);
	}, [res]);
	useEffect(() => {
		if (error) console.log(error);
	}, [error]);
	return (
		<Button type="primary" loading={loading} onClick={onClickFun}>
			init
		</Button>
	);
}
