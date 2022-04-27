import { Slider, InputNumber, Row, Col } from "antd";
import React, { useState, useEffect, useMemo } from "react";

export function InputScaler({ setOffset, setLimit }) {
	const [_offset, _setOffset] = useState(0);
	const [_limit, _setLimit] = useState(10);
	useEffect(() => {
		setOffset(_offset);
		setLimit(_limit);
	}, []);
	const onChangeOffset = (value) => {
		_setOffset(value);
		setOffset(value);
	};

	const onChangeLimit = (value) => {
		_setLimit(value);
		setLimit(value);
	};

	return (
		<>
			<Row>
				<Col>offset:</Col>
				<Col span={6}>
					<Slider
						min={0}
						max={30}
						onChange={onChangeOffset}
						value={typeof _offset === "number" ? _offset : 0}
					/>
				</Col>
				<Col span={4}>
					<InputNumber
						min={0}
						max={30}
						style={{ margin: "0 16px" }}
						value={_offset}
						onChange={onChangeOffset}
					/>
				</Col>
			</Row>

			<Row>
				<Col>limit:</Col>
				<Col span={6}>
					<Slider
						min={1}
						max={20}
						onChange={onChangeLimit}
						value={typeof _limit === "number" ? _limit : 0}
					/>
				</Col>
				<Col span={4}>
					<InputNumber
						min={1}
						max={20}
						style={{ margin: "0 16px" }}
						value={_limit}
						onChange={onChangeLimit}
					/>
				</Col>
			</Row>
		</>
	);
}
