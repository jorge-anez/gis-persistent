<?xml version="1.0" encoding="UTF-8"?>
<sld:StyledLayerDescriptor version="1.0.0"
                           xmlns:sld="http://www.opengis.net/sld"
                           xmlns:ogc="http://www.opengis.net/ogc"
                           xmlns:gml="http://www.opengis.net/gml"
                           xmlns:xlink="http://www.w3.org/1999/xlink"
                           xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                           xsi:schemaLocation="http://www.opengis.net/sld http://schemas.opengis.net/sld/1.0.0/StyledLayerDescriptor.xsd"
>

    <sld:NamedLayer>
        <sld:Name>style-template</sld:Name>
        <sld:UserStyle>
            <sld:Name>layerStyle</sld:Name>
            <sld:IsDefault>1</sld:IsDefault>

            <sld:FeatureTypeStyle>
                <Rule>
                    <ogc:Filter>
                        <ogc:Or>
                            <ogc:PropertyIsEqualTo>
                                <ogc:PropertyName>geometryType</ogc:PropertyName>
                                <ogc:Literal>Point</ogc:Literal>
                            </ogc:PropertyIsEqualTo>
                            <ogc:PropertyIsEqualTo>
                                <ogc:PropertyName>geometryType</ogc:PropertyName>
                                <ogc:Literal>MultiPoint</ogc:Literal>
                            </ogc:PropertyIsEqualTo>
                        </ogc:Or>
                    </ogc:Filter>
                    <PointSymbolizer>
                        <Graphic>
                            <Mark>
                                <WellKnownName>circle</WellKnownName>
                                <Fill>
                                    <CssParameter name="fill">#00FF00</CssParameter>
                                    <CssParameter name="fill-opacity">0.5</CssParameter>
                                </Fill>
                                <Stroke>
                                    <CssParameter name="stroke">#00FF00</CssParameter>
                                    <CssParameter name="stroke-width">2</CssParameter>
                                </Stroke>

                            </Mark>
                            <Size>6</Size>
                        </Graphic>
                    </PointSymbolizer>
                    <TextSymbolizer>
                        <Label>
                            <ogc:PropertyName>NOM_DEP</ogc:PropertyName>
                        </Label>
                        <Fill>
                            <CssParameter name="fill">#000000</CssParameter>
                        </Fill>
                    </TextSymbolizer>
                </Rule>
                <Rule>
                    <ogc:Filter>
                        <ogc:Or>
                            <ogc:PropertyIsEqualTo>
                                <ogc:PropertyName>geometryType</ogc:PropertyName>
                                <ogc:Literal>Line</ogc:Literal>
                            </ogc:PropertyIsEqualTo>
                            <ogc:PropertyIsEqualTo>
                                <ogc:PropertyName>geometryType</ogc:PropertyName>
                                <ogc:Literal>LineString</ogc:Literal>
                            </ogc:PropertyIsEqualTo>
                        </ogc:Or>
                    </ogc:Filter>
                    <LineSymbolizer>
                        <Stroke>
                            <CssParameter name="stroke">#00FF00</CssParameter>
                            <CssParameter name="stroke-width">2</CssParameter>
                        </Stroke>
                    </LineSymbolizer>
                    <TextSymbolizer>
                        <Label>
                            <ogc:PropertyName>NOM_DEP</ogc:PropertyName>
                        </Label>
                        <LabelPlacement>
                            <LinePlacement />
                        </LabelPlacement>
                        <Fill>
                            <CssParameter name="fill">#000000</CssParameter>
                        </Fill>
                    </TextSymbolizer>
                </Rule>
                <Rule>
                    <ogc:Filter>
                        <ogc:Or>
                            <ogc:PropertyIsEqualTo>
                                <ogc:PropertyName>geometryType</ogc:PropertyName>
                                <ogc:Literal>Polygon</ogc:Literal>
                            </ogc:PropertyIsEqualTo>
                            <ogc:PropertyIsEqualTo>
                                <ogc:PropertyName>geometryType</ogc:PropertyName>
                                <ogc:Literal>MultiPolygon</ogc:Literal>
                            </ogc:PropertyIsEqualTo>
                        </ogc:Or>
                    </ogc:Filter>
                    <PolygonSymbolizer>
                        <Fill>
                            <CssParameter name="fill">#f28d6d</CssParameter>
                            <CssParameter name="fill-opacity">0.5</CssParameter>
                        </Fill>
                        <Stroke>
                            <CssParameter name="stroke">#63533e</CssParameter>
                            <CssParameter name="stroke-width">2</CssParameter>
                        </Stroke>
                    </PolygonSymbolizer>
                    <TextSymbolizer>
                        <Label>
                            <ogc:PropertyName>NOM_MUN</ogc:PropertyName>
                        </Label>
                        <Halo>
                            <Radius>3</Radius>
                            <Fill>
                                 <CssParameter name="fill">#FFFFFF</CssParameter>
                            </Fill>
                        </Halo>
                    </TextSymbolizer>
                </Rule>

            </sld:FeatureTypeStyle>

        </sld:UserStyle>
    </sld:NamedLayer>

</sld:StyledLayerDescriptor>
